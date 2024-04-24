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

    public void getNearbyGyms(double latitudInferior, double longitudInferior, double latitudSuperior, double longitudSuperior, GymCallback callback) {
        new GetNearbyGymsTask(latitudInferior, longitudInferior, latitudSuperior, longitudSuperior, callback).execute();
    }
}