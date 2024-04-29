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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.eduardo.gymtracker.exercises.Exercise;
import es.eduardo.gymtracker.exercises.ExercisesFragment;
import es.eduardo.gymtracker.routines.Routine;
import es.eduardo.gymtracker.routines.RoutinePagerAdapter;


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
                    bundle.putString("muscleGroup", "Chest");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.back) {
                    bundle.putString("muscleGroup", "Back");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.arms) {
                    bundle.putString("muscleGroup", "Arms");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.legs) {
                    bundle.putString("muscleGroup", "Legs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.abs) {
                    bundle.putString("muscleGroup", "Abs");
                    fragment = new ExercisesFragment();
                } else if (id == R.id.shoulders) {
                    bundle.putString("muscleGroup", "Shoulders");
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
        db.collection("routines")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Routine> routines = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                List<Map<String, Object>> exercisesData = (List<Map<String, Object>>) document.get("exercises");
                                List<Exercise> exercises = new ArrayList<>();
                                for (Map<String, Object> exerciseData : exercisesData) {
                                    String exerciseName = (String) exerciseData.get("name");
                                    String exerciseDescription = (String) exerciseData.get("description");
                                    exercises.add(new Exercise(exerciseName, exerciseDescription));
                                }
                                int days = document.getLong("days").intValue();
                                routines.add(new Routine(name, exercises, days));
                            }
                            if (routines.isEmpty()) {
                                viewPager.setVisibility(View.GONE);
                                noRoutinesTextView.setVisibility(View.VISIBLE);
                            } else {
                                viewPager.setVisibility(View.VISIBLE);
                                noRoutinesTextView.setVisibility(View.GONE);
                                RoutinePagerAdapter routinePagerAdapter = new RoutinePagerAdapter(HomeFragment.this, routines);
                                viewPager.setAdapter(routinePagerAdapter);
                            }
                        } else {
                            Log.w("HomeFragment", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}