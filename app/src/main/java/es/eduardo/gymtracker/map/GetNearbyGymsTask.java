package es.eduardo.gymtracker.map;

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

import es.eduardo.gymtracker.gym.Gym;

public class GetNearbyGymsTask {
    private static final String OVERPASS_API_URL = "http://overpass-api.de/api/interpreter?data="; // URL de la API de Overpass

    // Coordenadas que delimitan la zona de búsqueda
    private double latitudInferior;
    private double longitudInferior;
    private double latitudSuperior;
    private double longitudSuperior;

    private GymCallback callback; // Interfaz para recibir los gimnasios encontrados
    private ExecutorService executorService; // Executor para ejecutar la tarea en un hilo en segundo plano
    private Handler handler; // Handler para ejecutar código en el hilo principal

    public GetNearbyGymsTask(double latitudInferior, double longitudInferior, double latitudSuperior, double longitudSuperior, GymCallback callback) {
        this.latitudInferior = latitudInferior;
        this.longitudInferior = longitudInferior;
        this.latitudSuperior = latitudSuperior;
        this.longitudSuperior = longitudSuperior;
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

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

    protected List<Gym> doInBackground() {
        String query = "[out:json];node[\"leisure\"=\"fitness_centre\"](" +
                (latitudInferior) + "," + (longitudInferior) + "," +
                (latitudSuperior) + "," + (longitudSuperior) + ");out;";
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

    protected void onPostExecute(List<Gym> gyms) {
        callback.onGymsReceived(gyms);
    }

    private List<Gym> parseGymsFromJson(String json) {
        List<Gym> gyms = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");

                // Se obtienen los datos del gimnasio desde el JSON
                // Se utiliza optString en lugar de getString para evitar excepciones en caso de que no exista el campo
                String name = element.getJSONObject("tags").optString("name");
                String street = element.getJSONObject("tags").optString("addr:street");
                String number = element.getJSONObject("tags").optString("addr:housenumber");
                String city = element.getJSONObject("tags").optString("addr:city");
                String postalCode = element.getJSONObject("tags").optString("addr:postcode");
                String phoneNumber = element.getJSONObject("tags").optString("phone");

                String address = street + " " + number + ", " + city + ", " + postalCode;

                gyms.add(new Gym(name,address,phoneNumber ,lat, lon));
                Log.d("OverpassAPI", "Parsed " + gyms.size() + " gyms from JSON");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OverpassAPI", "Error parsing gyms from JSON", e);
        }

        return gyms;
    }
}