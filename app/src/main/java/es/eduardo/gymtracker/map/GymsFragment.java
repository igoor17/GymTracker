package es.eduardo.gymtracker.map;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.eduardo.gymtracker.R;

import org.osmdroid.config.Configuration;
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

        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getContext()), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);

        overpassAPI = new OverpassAPI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        locationOverlay.enableMyLocation();

        GeoPoint myLocation = locationOverlay.getMyLocation();
        if(myLocation != null) {
            Log.d("GymsFragment", "Ubicación del usuario obtenida: " + myLocation.getLatitude() + ", " + myLocation.getLongitude());

            mapView.getController().setCenter(myLocation);
            mapView.getController().setZoom(15.0);

            List<Gimnasio> gimnasios = overpassAPI.getNearbyGyms(myLocation.getLatitude(), myLocation.getLongitude());

            if (gimnasios.isEmpty()) {
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
            }

            mapView.invalidate();
        } else {
            Log.d("GymsFragment", "No se pudo obtener la ubicación del usuario");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        locationOverlay.disableMyLocation();
    }
}