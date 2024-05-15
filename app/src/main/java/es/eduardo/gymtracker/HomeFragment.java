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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Button a;

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

        a=view.findViewById(R.id.buttonA);

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

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveExercise("Bench Press", "Descend the bar smoothly toward the chest while inhaling. After 1 second, push the bar back to the starting position while exhaling. Repeat the movement until you complete the repetitions, and then place the bar back on the rack.\n" +
                        "\n"+
                        "TIPS: Focus on pushing the bar with the chest muscles and squeeze your glutes as you do so. If you are a beginner, use a spotter. If there isn't one, be conservative with the load you use.", "Chest", "chest");


            }
        });

        return view;
    }

    private void saveExercise(String exerciseName, String description, String muscleGroup, String muscle) {
        Map<String, Object> exercise = new HashMap<>();
        exercise.put("name", exerciseName);
        exercise.put("description", description);
        exercise.put("imageUrl", "");
        exercise.put("muscleGroup", muscleGroup);

        db.collection("muscleGroup").document(muscle).collection("exercises")
                .document(exerciseName)
                .set(exercise)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("HomeFragment", "Exercise successfully written to Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("HomeFragment", "Error writing exercise to Firestore", e);
                    }
                });
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
                                String imageUrl = document.getString("imageUrl"); // Obtener la URL de la imagenº
                                Long daysLong = document.getLong("days"); // Obtener el número de días
                                Long totalExercisesLong = document.getLong("exercises"); // Obtener el número total de ejercicios

                                int days = daysLong != null ? daysLong.intValue() : 0;
                                int totalExercises = totalExercisesLong != null ? totalExercisesLong.intValue() : 0;

                                routines.add(new Routine(name, imageUrl, days, totalExercises, new ArrayList<>()));

                                if (routines.isEmpty()) {
                                    viewPager.setVisibility(View.GONE);
                                    noRoutinesTextView.setVisibility(View.VISIBLE);
                                } else {
                                    viewPager.setVisibility(View.VISIBLE);
                                    noRoutinesTextView.setVisibility(View.GONE);
                                    if (isAdded()) {
                                        RoutineAdapter routineAdapter = new RoutineAdapter(HomeFragment.this, routines);
                                        viewPager.setAdapter(routineAdapter);
                                    }
                                }
                            }
                        } else {
                            Log.w("HomeFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}