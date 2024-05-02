package es.eduardo.gymtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.exercises.ExercisesFragment;
import es.eduardo.gymtracker.routines.Routine;
import es.eduardo.gymtracker.routines.RoutineAdapter;


public class HomeFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Muscle Groups
    ImageView chestImageView;
    ImageView backImageView;
    ImageView armsImageView;
    ImageView legsImageView;

    // User Routines
    ViewPager2 viewPager;
    TextView noRoutinesTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        chestImageView = view.findViewById(R.id.chest);
        backImageView = view.findViewById(R.id.back);
        armsImageView = view.findViewById(R.id.arms);
        legsImageView = view.findViewById(R.id.legs);

        viewPager = view.findViewById(R.id.viewPager);
        noRoutinesTextView = view.findViewById(R.id.noRoutinesTextView);

        loadRoutines();

        View.OnClickListener imageViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                int id = v.getId();
                if (id == R.id.chest) {
                    bundle.putString("muscleGroup", "chest");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.back) {
                    bundle.putString("muscleGroup", "back");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.arms) {
                    bundle.putString("muscleGroup", "arms");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.legs) {
                    bundle.putString("muscleGroup", "legs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.abs) {
                    bundle.putString("muscleGroup", "abs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.shoulders) {
                    bundle.putString("muscleGroup", "shoulders");
                    fragment = new ExercisesFragment();
                } else {
                    throw new IllegalStateException("Unexpected value: " + id);
                }

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        chestImageView.setOnClickListener(imageViewClickListener);
        backImageView.setOnClickListener(imageViewClickListener);
        armsImageView.setOnClickListener(imageViewClickListener);
        legsImageView.setOnClickListener(imageViewClickListener);

        return view;
    }

    private void loadRoutines() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("users").document(userEmail).collection("routines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Routine> routines = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId(); // Obtener el nombre de la rutina
                                String imageUrl = document.getString("imageUrl"); // Asegúrate de tener este campo en Firestore
                                db.collection("users").document(userEmail).collection("routines").document(name).collection("exercises")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    int days = task.getResult().size();
                                                    routines.add(new Routine(name, imageUrl, days, new ArrayList<>()));
                                                    if (routines.isEmpty()) {
                                                        viewPager.setVisibility(View.GONE);
                                                        noRoutinesTextView.setVisibility(View.VISIBLE);
                                                    } else {
                                                        viewPager.setVisibility(View.VISIBLE);
                                                        noRoutinesTextView.setVisibility(View.GONE);
                                                        RoutineAdapter routineAdapter = new RoutineAdapter(HomeFragment.this, routines);
                                                        viewPager.setAdapter(routineAdapter);
                                                    }
                                                } else {
                                                    Log.w("HomeFragment", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.w("HomeFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}