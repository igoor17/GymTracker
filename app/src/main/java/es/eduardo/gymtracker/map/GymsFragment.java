package es.eduardo.gymtracker.map;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.eduardo.gymtracker.R;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class GymsFragment extends Fragment {

    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private OverpassAPI overpassAPI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gyms, container, false);

        // Configuración de osmdroid
        Configuration.getInstance().load(getContext(), PreferenceManager.getDefaultSharedPreferences(getContext()));

        mapView = view.findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true);

        mapView.getController().setZoom(20.0);

        GpsMyLocationProvider provider = new GpsMyLocationProvider(getContext());
        provider.addLocationSource(LocationManager.GPS_PROVIDER);

        locationOverlay = new MyLocationNewOverlay(provider, mapView);
        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(locationOverlay);

        overpassAPI = new OverpassAPI();

        mapView.addMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                updateGymsOnMap();
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                updateGymsOnMap();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        locationOverlay.enableMyLocation();

        // Obtén el área visible del mapa
        BoundingBox visibleArea = mapView.getBoundingBox();
        // Obtén los gimnasios en el área visible
        overpassAPI.getNearbyGyms(
                visibleArea.getLatSouth(),
                visibleArea.getLonWest(),
                visibleArea.getLatNorth(),
                visibleArea.getLonEast(),
                new GymCallback() {
                    @Override
                    public void onGymsReceived(List<Gimnasio> gimnasios) {
                        if (gimnasios == null || gimnasios.isEmpty()) {
                            Log.d("GymsFragment", "No se encontraron gimnasios cercanos");
                        } else {
                            Log.d("GymsFragment", "Se encontraron " + gimnasios.size() + " gimnasios cercanos");
                        }

                        for (Gimnasio gimnasio : gimnasios) {
                            GeoPoint gymLocation = new GeoPoint(gimnasio.getLatitud(), gimnasio.getLongitud());

                            Marker gymMarker = new Marker(mapView);
                            gymMarker.setPosition(gymLocation);
                            gymMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            gymMarker.setTitle(gimnasio.getNombre());

                            mapView.getOverlays().add(gymMarker);
                            Log.d("GymsFragment", "Added gym marker to map: " + gimnasio.getNombre());
                        }

                        // Actualiza el mapa después de añadir todos los marcadores
                        mapView.invalidate();
                    }
                }
        );
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locationOverlay.disableMyLocation();
    }

    private void updateGymsOnMap() {
        // Obtén el área visible del mapa
        BoundingBox visibleArea = mapView.getBoundingBox();

        // Obtén los gimnasios en el área visible
        overpassAPI.getNearbyGyms(
                visibleArea.getLatSouth(),
                visibleArea.getLonWest(),
                visibleArea.getLatNorth(),
                visibleArea.getLonEast(),
                new GymCallback() {
                    @Override
                    public void onGymsReceived(List<Gimnasio> gimnasios) {
                        // Elimina los marcadores existentes
                        mapView.getOverlays().clear();

                        // Añade nuevos marcadores para los gimnasios en el área visible
                        for (Gimnasio gimnasio : gimnasios) {
                            GeoPoint gymLocation = new GeoPoint(gimnasio.getLatitud(), gimnasio.getLongitud());

                            Marker gymMarker = new Marker(mapView);
                            gymMarker.setPosition(gymLocation);
                            gymMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            gymMarker.setTitle(gimnasio.getNombre());

                            mapView.getOverlays().add(gymMarker);
                        }

                        // Actualiza el mapa
                        mapView.invalidate();
                    }
                }
        );
    }
}