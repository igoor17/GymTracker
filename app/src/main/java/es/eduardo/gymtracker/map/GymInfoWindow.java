package es.eduardo.gymtracker.map;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;

import java.util.HashMap;
import java.util.Map;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.gym.Gym;

public class GymInfoWindow extends BasicInfoWindow {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Gimnasio
    private Gym gym;

    public GymInfoWindow(int layoutResId, MapView mapView, Gym gym, FirebaseAuth mAuth, FirebaseFirestore db) {
        super(layoutResId, mapView);
        this.gym = gym;
        this.mAuth = mAuth;
        this.db = db;
    }

    @Override
    public void onOpen(Object item) {

        TextView gymName = (TextView) mView.findViewById(R.id.gym_name);
        TextView gymAddress = (TextView) mView.findViewById(R.id.gym_address);
        TextView gymPhone = (TextView) mView.findViewById(R.id.gym_phone_number);

        gymName.setText(gym.getNombre() != null ? gym.getNombre() : "");
        gymAddress.setText(gym.getAddress() != null ? gym.getAddress() : "");
        gymPhone.setText(gym.getPhoneNumber() != null ? gym.getPhoneNumber() : "");

        Button btnAddToFavorites = (Button) mView.findViewById(R.id.btn_add_to_favorites);
        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGymToFavorites(gym);
            }
        });
    }

    private void saveGymToFavorites(Gym gimnasio) {
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
                        Toast.makeText(mView.getContext(), mView.getContext().getString(R.string.add_gym_fav), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("GymFragment", "Error adding gym to favorites", e);
                        Toast.makeText(mView.getContext(), mView.getContext().getString(R.string.error_gym_fav), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
