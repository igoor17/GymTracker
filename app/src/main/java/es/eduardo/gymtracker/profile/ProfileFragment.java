package es.eduardo.gymtracker.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import es.eduardo.gymtracker.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    // Firebase
    private FirebaseUser user;
    private FirebaseFirestore db;

    // UI
    private ImageButton editProfileButton;
    private ImageButton settingsButton;

    // Info Usuario
    TextView profileName;
    TextView profileEmail;
    TextView profileAge;
    TextView profileHeight;
    TextView profileWeight;
    TextView profileImc;
    ImageView profileImage;

    // Progress

    Button progressButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

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
        String userEmail=user.getEmail();

        // Obtén la información del usuario y establece los valores en los TextViews
        db.collection("users").document(userEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (isAdded()) {
                            getActivity().runOnUiThread(() -> {
                                profileName.setText(documentSnapshot.getString("name"));
                                profileEmail.setText(userEmail);
                                profileAge.setText(documentSnapshot.getString("age"));
                                profileHeight.setText(documentSnapshot.getString("height"));
                                profileWeight.setText(documentSnapshot.getString("weight"));
                                profileImc.setText(String.format(Locale.getDefault(),"%.2f",documentSnapshot.getDouble("bmi")));

                                // Cargar la imagen del usuario en el ImageView
                                String imageUrl = documentSnapshot.getString("imageUrl");
                                if (imageUrl != null) {
                                    Glide.with(getActivity())
                                            .load(imageUrl)
                                            .into(profileImage);
                                }
                            });
                        }
                    }
                });

        showProgress();

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar al fragmento EditProfileFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new EditProfileFragment());
                transaction.commit();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar al fragmento SettingsFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new SettingsFragment());
                transaction.commit();
            }
        });

        return view;
    }

    private void showProgress(){
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar al nuevo fragmento de progreso
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new ProgressFragment());
                transaction.commit();
            }
        });
    }
}