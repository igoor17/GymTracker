package es.eduardo.gymtracker.map;

/**
 * This class interacts with the Overpass API to fetch nearby gyms based on geographical coordinates.
 */
public class OverpassAPI {

    /**
     * Fetches nearby gyms using the Overpass API within the specified geographical bounds.
     *
     * @param latitudInferior Lower latitude bound.
     * @param longitudInferior Lower longitude bound.
     * @param latitudSuperior Upper latitude bound.
     * @param longitudSuperior Upper longitude bound.
     * @param callback        Callback to receive the result asynchronously.
     */
    public void getNearbyGyms(double latitudInferior, double longitudInferior,
                              double latitudSuperior, double longitudSuperior, GymCallback callback) {
        new GetNearbyGymsTask(latitudInferior, longitudInferior, latitudSuperior, longitudSuperior, callback).execute();
    }
}
