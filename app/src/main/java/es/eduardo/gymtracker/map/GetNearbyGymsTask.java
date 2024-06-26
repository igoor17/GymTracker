package es.eduardo.gymtracker.map;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.gym.Gym;

/**
 * Task to retrieve nearby gyms using Overpass API.
 */
public class GetNearbyGymsTask {
    private static final String OVERPASS_API_URL = "http://overpass-api.de/api/interpreter?data="; // URL of Overpass API

    private double latitudInferior; // Lower latitude boundary of the search area
    private double longitudInferior; // Lower longitude boundary of the search area
    private double latitudSuperior; // Upper latitude boundary of the search area
    private double longitudSuperior; // Upper longitude boundary of the search area

    private Context context; // Context to access resources

    private GymCallback callback; // Interface to receive found gyms
    private ExecutorService executorService; // Executor to run the task on a background thread
    private Handler handler; // Handler to execute code on the main thread

    /**
     * Constructor to initialize the task with search boundaries and callback.
     *
     * @param latitudInferior  Lower latitude boundary of the search area.
     * @param longitudInferior Lower longitude boundary of the search area.
     * @param latitudSuperior  Upper latitude boundary of the search area.
     * @param longitudSuperior Upper longitude boundary of the search area.
     * @param callback         Callback to receive gyms found.
     */
    public GetNearbyGymsTask(Context context,double latitudInferior, double longitudInferior, double latitudSuperior, double longitudSuperior, GymCallback callback) {
        this.context = context;
        this.latitudInferior = latitudInferior;
        this.longitudInferior = longitudInferior;
        this.latitudSuperior = latitudSuperior;
        this.longitudSuperior = longitudSuperior;
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    /**
     * Executes the task to fetch nearby gyms asynchronously.
     */
    public void execute() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Gym> gyms = doInBackground();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(gyms);
                    }
                });
            }
        });
    }

    /**
     * Background task to fetch nearby gyms using Overpass API.
     *
     * @return List of Gym objects found.
     */
    protected List<Gym> doInBackground() {
        String query = "[out:json];node[\"leisure\"=\"fitness_centre\"](" +
                latitudInferior + "," + longitudInferior + "," +
                latitudSuperior + "," + longitudSuperior + ");out;";
        List<Gym> gyms = new ArrayList<>();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(OVERPASS_API_URL + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d("OverpassAPI", "Response code: " + responseCode);
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.d("OverpassAPI", "Failed to get data from Overpass API");
                return gyms; // return the empty list instead of null
            }

            inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            return parseGymsFromJson(scanner.hasNext() ? scanner.next() : "");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return gyms;
    }

    /**
     * Callback method executed on the main thread after fetching gyms.
     *
     * @param gyms List of Gym objects received.
     */
    protected void onPostExecute(List<Gym> gyms) {
        callback.onGymsReceived(gyms);
    }

    /**
     * Parses the JSON response from Overpass API to extract Gym objects.
     *
     * @param json JSON string received from Overpass API.
     * @return List of Gym objects parsed from JSON.
     */
    private List<Gym> parseGymsFromJson(String json) {
        List<Gym> gyms = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");

                // Get gym data from JSON
                String name = element.getJSONObject("tags").optString("name");
                String street = element.getJSONObject("tags").optString("addr:street");
                String number = element.getJSONObject("tags").optString("addr:housenumber");
                String city = element.getJSONObject("tags").optString("addr:city");
                String postalCode = element.getJSONObject("tags").optString("addr:postcode");
                String phoneNumber = element.getJSONObject("tags").optString("phone");

                //si la calle, el número, la ciudad o el código postal están vacíos, se muestra un @string de Address not available
                String address;
                if (street.isEmpty() || number.isEmpty() || city.isEmpty() || postalCode.isEmpty()) {
                    address = context.getString(R.string.address_not_available) ;
                } else {
                    address = street + " " + number + ", " + postalCode + " " + city;
                }

                gyms.add(new Gym(name, address, phoneNumber, lat, lon));
                Log.d("OverpassAPI", "Parsed " + gyms.size() + " gyms from JSON");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OverpassAPI", "Error parsing gyms from JSON", e);
        }

        return gyms;
    }
}



