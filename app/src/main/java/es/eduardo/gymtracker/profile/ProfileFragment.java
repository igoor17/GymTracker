package es.eduardo.gymtracker.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private FirebaseUser user;
    private FirebaseFirestore db;
    private ImageButton editProfileButton;
    TextView profileName;
    TextView profileAge;
    TextView profileHeight;
    TextView profileWeight;
    TextView profileImc;
    ImageView profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        profileName = view.findViewById(R.id.profile_name);
        profileAge = view.findViewById(R.id.profile_age);
        profileHeight = view.findViewById(R.id.profile_height);
        profileWeight = view.findViewById(R.id.profile_weight);
        profileImc = view.findViewById(R.id.profile_imc);
        profileImage = view.findViewById(R.id.profile_image);

        // Obtén la información del usuario y establece los valores en los TextViews
        db.collection("users").document(user.getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        profileName.setText(documentSnapshot.getString("name"));
                        profileAge.setText(documentSnapshot.getString("age"));
                        profileHeight.setText(documentSnapshot.getString("height"));
                        profileWeight.setText(documentSnapshot.getString("weight"));
                        profileImc.setText(String.valueOf(documentSnapshot.getDouble("bmi")));

                        // Cargar la imagen del usuario en el ImageView
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null) {
                            Glide.with(getActivity())
                                    .load(imageUrl)
                                    .into(profileImage);
                        }
                    }
                });

        editProfileButton = view.findViewById(R.id.edit_profile_button);
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar al fragmento EditProfileFragment
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new EditProfileFragment());
                transaction.commit();
            }
        });

        return view;
    }
}