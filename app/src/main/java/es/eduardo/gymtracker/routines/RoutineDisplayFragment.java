package es.eduardo.gymtracker.routines;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.Exercise;
import es.eduardo.gymtracker.exercises.ExerciseAdapter;
import es.eduardo.gymtracker.exercises.ExerciseDisplayFragment;

/**
 * Fragment for displaying details of a specific routine, including exercises for each day of the week.
 */
public class RoutineDisplayFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String routineName;
    private RecyclerView exercisesRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private TextView noExercisesTextView;

    // Mapping of chip numbers to English names of days of the week
    private String[] daysOfWeek;

    /**
     * Inflates the layout for this fragment and initializes necessary UI components.
     *
     * @param inflater           LayoutInflater to inflate the layout.
     * @param container          ViewGroup container for the fragment UI.
     * @param savedInstanceState Saved state of the fragment.
     * @return Inflated View of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine_display, container, false);

        TextView routineNameTextView = view.findViewById(R.id.routine_name);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);
        exercisesRecyclerView = view.findViewById(R.id.exercises_recycler_view);
        noExercisesTextView = view.findViewById(R.id.no_exercises_text_view);

        routineName = getArguments().getString("routineName");
        routineNameTextView.setText(routineName);

        // Get days of the week from string-array resources
        daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        exerciseAdapter = new ExerciseAdapter(new ArrayList<>(), this::onExerciseSelected);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                // Get the day of the week number from the chip text and use it to get the English day name
                int dayOfWeekNumber = Integer.parseInt(chip.getText().toString()) - 1;
                String dayOfWeek = daysOfWeek[dayOfWeekNumber];
                Log.d("RoutineDisplayFragment", "Day: " + dayOfWeek);
                loadExercisesForDay(dayOfWeek);
            }
        });

        return view;
    }

    /**
     * Loads exercises for a specific day of the week from Firestore.
     *
     * @param dayOfWeek English name of the day of the week.
     */
    private void loadExercisesForDay(String dayOfWeek) {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        Log.d("RoutineDisplayFragment", "Loading exercises for day: " + dayOfWeek);

        db.collection("users").document(userEmail).collection("routines")
                .document(routineName).collection("exercises")
                .document(dayOfWeek).collection("exercises")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Exercise> exercises = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String name = documentSnapshot.getId();
                            exercises.add(new Exercise(name));
                            Log.d("RoutineDisplayFragment", "Exercise: " + name);
                        }
                        if (exercises.isEmpty()) {
                            noExercisesTextView.setVisibility(View.VISIBLE);
                            exercisesRecyclerView.setVisibility(View.GONE);
                        } else {
                            noExercisesTextView.setVisibility(View.GONE);
                            exercisesRecyclerView.setVisibility(View.VISIBLE);
                            exerciseAdapter.updateExercises(exercises);
                        }
                    }
                });
    }

    /**
     * Handles the event when an exercise is selected.
     * Navigates to ExerciseDisplayFragment to display details of the selected exercise.
     *
     * @param exercise The selected exercise.
     */
    public void onExerciseSelected(Exercise exercise) {
        // Create a new instance of ExerciseDisplayFragment
        ExerciseDisplayFragment exerciseDisplayFragment = new ExerciseDisplayFragment();

        // Create a new Bundle to pass the selected exercise to ExerciseDisplayFragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_exercise", exercise);
        exerciseDisplayFragment.setArguments(bundle);

        // Navigate to ExerciseDisplayFragment
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, exerciseDisplayFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
