package es.eduardo.gymtracker.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.gym.FavGymsFragment;

/**
 * Fragmento que muestra el perfil del usuario y permite realizar acciones como
 * editar el perfil, ver progreso y favoritos.
 */
public class ProfileFragment extends Fragment {

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore db;

    // UI
    private ImageButton editProfileButton;
    private ImageButton settingsButton;
    private TextView profileName;
    private TextView profileEmail;
    private TextView profileAge;
    private TextView profileHeight;
    private TextView profileWeight;
    private TextView profileImc;
    private ImageView profileImage;
    private Button progressButton;
    private Button favGymsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase instances
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        settingsButton = view.findViewById(R.id.settings_button);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        profileAge = view.findViewById(R.id.profile_age);
        profileHeight = view.findViewById(R.id.profile_height);
        profileWeight = view.findViewById(R.id.profile_weight);
        profileImc = view.findViewById(R.id.profile_imc);
        profileImage = view.findViewById(R.id.profile_image);
        progressButton = view.findViewById(R.id.progressButton);
        favGymsButton = view.findViewById(R.id.favGymsButton);

        // Set click listener for favorite gyms button
        favGymsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to FavGymsFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new FavGymsFragment());
                transaction.commit();
            }
        });

        // Retrieve user information from Firestore and set values in TextViews
        db.collection("users").document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (isAdded() && documentSnapshot.exists()) {
                            getActivity().runOnUiThread(() -> {
                                profileName.setText(documentSnapshot.getString("name"));
                                profileEmail.setText(user.getEmail());
                                profileAge.setText(documentSnapshot.getString("age"));
                                profileHeight.setText(documentSnapshot.getString("height"));
                                profileWeight.setText(documentSnapshot.getString("weight"));
                                Double bmi = documentSnapshot.getDouble("bmi");
                                if (bmi != null) {
                                    profileImc.setText(String.format(Locale.getDefault(), "%.2f", bmi));
                                }

                                // Load user image into ImageView using Glide
                                String imageUrl = documentSnapshot.getString("imageUrl");
                                if (imageUrl != null) {
                                    Glide.with(requireActivity())
                                            .load(imageUrl)
                                            .into(profileImage);
                                }
                            });
                        }
                    }
                });

        // Set click listener for edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to EditProfileFragment
                FragmentTransaction transaction = requireFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new EditProfileFragment());
                transaction.commit();
            }
        });

        // Set click listener for settings button
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SettingsFragment
                FragmentTransaction transaction = requireFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new SettingsFragment());
                transaction.commit();
            }
        });

        // Set click listener for progress button
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProgressFragment
                FragmentTransaction transaction = requireFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new ProgressFragment());
                transaction.commit();
            }
        });

        return view;
    }
}
