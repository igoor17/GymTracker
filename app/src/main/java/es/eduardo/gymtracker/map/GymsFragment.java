package es.eduardo.gymtracker.map;

import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.gym.Gym;

import org.checkerframework.checker.nullness.qual.NonNull;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GymsFragment extends Fragment {

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    // Mapa
    private MapView mapView;
    private MyLocationNewOverlay locationOverlay;
    private OverpassAPI overpassAPI;

    // Executor
    private Executor executor;

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
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(true);
        mapView.getOverlays().add(locationOverlay);

        mapView.addMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (locationOverlay != null) {
                    locationOverlay.disableFollowLocation();
                }
                updateGymsOnMap();
                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                if (locationOverlay != null) {
                    locationOverlay.disableFollowLocation();
                }
                updateGymsOnMap();
                return true;
            }
        }, 1000));

        overpassAPI = new OverpassAPI();

        // Initialize the executor
        executor = Executors.newSingleThreadExecutor();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        locationOverlay.enableMyLocation();

    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationOverlay != null) {
            locationOverlay.disableMyLocation();
            locationOverlay = null;
        }
        if (mapView != null) {
            mapView.onDetach();
            mapView = null;
        }
    }

    private void updateGymsOnMap() {
        if(mapView == null) {
            Log.e("GymsFragment", "MapView is null");
            return;
        }
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
                    public void onGymsReceived(List<Gym> gyms) {
                        // Elimina los marcadores existentes
                        mapView.getOverlays().clear();

                        // Añade nuevos marcadores para los gimnasios en el área visible
                        for (Gym gym : gyms) {
                            GeoPoint gymLocation = new GeoPoint(gym.getLatitud(), gym.getLongitud());

                            if (mapView != null) {
                                Marker gymMarker = new Marker(mapView);
                                gymMarker.setPosition(gymLocation);
                                gymMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                                gymMarker.setTitle(gym.getNombre());

                                gymMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker, MapView mapView) {
                                        gymMarker.setInfoWindow(new GymInfoWindow(R.layout.popup_gyms, mapView, gym, mAuth, db));
                                        gymMarker.showInfoWindow();
                                        return true;
                                    }
                                });

                                mapView.getOverlays().add(gymMarker);
                            } else {
                                Log.e("GymsFragment", "MapView is null");
                                return;
                            }
                        }

                        mapView.getOverlays().add(locationOverlay);

                        // Actualiza el mapa
                        mapView.invalidate();
                    }
                }
        );
    }


    private void saveGymToFavorites(Gym gimnasio) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Obtiene la instancia de Firestore y de FirebaseAuth
                db = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();

                // Obtén el ID del usuario actual
                String userEmail = mAuth.getCurrentUser().getEmail();

                // Crea un nuevo documento para el gimnasio
                Map<String, Object> gym = new HashMap<>();
                gym.put("name", gimnasio.getNombre());
                gym.put("address", gimnasio.getAddress());
                gym.put("phone", gimnasio.getPhoneNumber());
                gym.put("lat", gimnasio.getLatitud());
                gym.put("lon", gimnasio.getLongitud());

                // Guarda el gimnasio en la colección de favoritos del usuario
                db.collection("users").document(userEmail).collection("favorites").document(gimnasio.getNombre()).set(gym)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("GymFragment", "Gym added to favorites with ID: " + gimnasio.getNombre());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("GymFragment", "Error adding gym to favorites", e);
                            }
                        });
            }
        });
    }
}