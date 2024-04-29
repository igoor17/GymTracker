package es.eduardo.gymtracker.map;

public class OverpassAPI {

    public void getNearbyGyms(double latitudInferior, double longitudInferior, double latitudSuperior, double longitudSuperior, GymCallback callback) {
        new GetNearbyGymsTask(latitudInferior, longitudInferior, latitudSuperior, longitudSuperior, callback).execute();
    }
}