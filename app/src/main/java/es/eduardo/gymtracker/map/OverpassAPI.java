package es.eduardo.gymtracker.map;

import android.util.Log;

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

    public void getNearbyGyms(double latitudInferior, double longitudInferior, double latitudSuperior, double longitudSuperior, GymCallback callback) {
        new GetNearbyGymsTask(latitudInferior, longitudInferior, latitudSuperior, longitudSuperior, callback).execute();
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
                Log.d("OverpassAPI", "Parsed " + gimnasios.size() + " gyms from JSON");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("OverpassAPI", "Error parsing gyms from JSON", e);
        }

        return gimnasios;
    }
}