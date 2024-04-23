package es.eduardo.gymtracker.map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OverpassAPI {

    private static final String OVERPASS_API_URL = "http://overpass-api.de/api/interpreter?data=";

    public List<Gimnasio> getNearbyGyms(double latitude, double longitude) {
        String query = "[out:json];node[\"leisure\"=\"fitness_centre\"](" +
                (latitude - 0.01) + "," + (longitude - 0.01) + "," +
                (latitude + 0.01) + "," + (longitude + 0.01) + ");out;";

        try {
            URL url = new URL(OVERPASS_API_URL + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            return parseGymsFromJson(scanner.hasNext() ? scanner.next() : "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private List<Gimnasio> parseGymsFromJson(String json) {
        List<Gimnasio> gimnasios = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray elements = jsonObject.getJSONArray("elements");

            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");

                // Aquí asumimos que cada gimnasio tiene un nombre, lo cual puede no ser cierto en todos los casos
                String name = element.getJSONObject("tags").getString("name");

                gimnasios.add(new Gimnasio(name, lat, lon));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gimnasios;
    }
}