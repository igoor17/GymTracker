package es.eduardo.gymtracker.map;

public class Gimnasio {
    private String nombre;
    private double latitud;
    private double longitud;
    private String address;
    private String phoneNumber;

    public Gimnasio(String nombre,String address ,String phoneNumber,double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}