package es.eduardo.gymtracker.gym;

/**
 * Represents a Gym with a name, address, phone number, and geographical coordinates.
 */
public class Gym {
    private String nombre;
    private double latitud;
    private double longitud;
    private String address;
    private String phoneNumber;

    /**
     * Constructs a Gym with the specified details.
     *
     * @param nombre       the name of the gym
     * @param address      the address of the gym
     * @param phoneNumber  the phone number of the gym
     * @param latitud      the latitude of the gym's location
     * @param longitud     the longitude of the gym's location
     */
    public Gym(String nombre, String address, String phoneNumber, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the name of the gym.
     *
     * @return the name of the gym
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Gets the latitude of the gym's location.
     *
     * @return the latitude of the gym's location
     */
    public double getLatitud() {
        return latitud;
    }

    /**
     * Gets the longitude of the gym's location.
     *
     * @return the longitude of the gym's location
     */
    public double getLongitud() {
        return longitud;
    }

    /**
     * Gets the address of the gym.
     *
     * @return the address of the gym
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the phone number of the gym.
     *
     * @return the phone number of the gym
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
