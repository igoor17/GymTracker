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

/**
 * Custom InfoWindow for displaying Gym information and handling interactions.
 */
public class GymInfoWindow extends BasicInfoWindow {

    // Firebase
    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firebase Firestore instance

    // Gym
    private Gym gym; // Gym object to display information

    /**
     * Constructor for GymInfoWindow.
     *
     * @param layoutResId Layout resource ID for the info window.
     * @param mapView     MapView associated with the info window.
     * @param gym         Gym object to display information.
     * @param mAuth       FirebaseAuth instance for user authentication.
     * @param db          FirebaseFirestore instance for Firestore database access.
     */
    public GymInfoWindow(int layoutResId, MapView mapView, Gym gym, FirebaseAuth mAuth, FirebaseFirestore db) {
        super(layoutResId, mapView);
        this.gym = gym;
        this.mAuth = mAuth;
        this.db = db;
    }

    /**
     * Called when the info window is opened.
     *
     * @param item The item associated with the info window.
     */
    @Override
    public void onOpen(Object item) {
        // Initialize views
        TextView gymName = mView.findViewById(R.id.gym_name);
        TextView gymAddress = mView.findViewById(R.id.gym_address);
        TextView gymPhone = mView.findViewById(R.id.gym_phone_number);

        // Set gym information to views
        gymName.setText(gym.getNombre() != null ? gym.getNombre() : "");
        gymAddress.setText(gym.getAddress() != null ? gym.getAddress() : "");
        gymPhone.setText(gym.getPhoneNumber() != null ? gym.getPhoneNumber() : "");

        // Button to add gym to favorites
        Button btnAddToFavorites = mView.findViewById(R.id.btn_add_to_favorites);
        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGymToFavorites(gym);
            }
        });
    }

    /**
     * Saves the gym to user's favorites in Firestore.
     *
     * @param gimnasio The Gym object to be saved.
     */
    private void saveGymToFavorites(Gym gimnasio) {
        // Get Firestore and FirebaseAuth instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get current user's email ID
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Create a new document for the gym
        Map<String, Object> gym = new HashMap<>();
        gym.put("name", gimnasio.getNombre());
        gym.put("address", gimnasio.getAddress());
        gym.put("phone", gimnasio.getPhoneNumber());
        gym.put("lat", gimnasio.getLatitud());
        gym.put("lon", gimnasio.getLongitud());

        // Save the gym to the user's favorites collection
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
