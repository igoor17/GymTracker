package es.eduardo.gymtracker.routines;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.Exercise;
import es.eduardo.gymtracker.exercises.ExerciseAdapter;
import com.google.android.material.chip.ChipGroup;

/**
 * Fragment for displaying exercises for a specific day of the week and managing their selection.
 */
public class DayFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Arguments
    public static final String ARG_DAY = "day"; // Key used to pass the day as an argument between fragments

    // Variables
    private int day; // Day of the week for which the routine is being created
    private List<Exercise> selectedExercises; // List of exercises selected for the specified day
    private String muscleGroup; // Selected muscle group to filter exercises

    // UI
    private RecyclerView exercisesRecyclerView; // RecyclerView for displaying available exercises
    private RecyclerView selectedExercisesRecyclerView; // RecyclerView for displaying selected exercises
    private ChipGroup muscleGroupChipGroup; // ChipGroup for muscle group selection

    // Adapters
    private ExerciseAdapter exercisesAdapter;
    private ExerciseAdapter selectedExercisesAdapter;

    /**
     * Creates a new instance of DayFragment with the given day and selected exercises.
     *
     * @param day               The day of the week (1-7, where 1 is Monday).
     * @param selectedExercises List of exercises already selected for this day.
     * @return A new instance of DayFragment.
     */
    public static DayFragment newInstance(int day, List<Exercise> selectedExercises) {
        DayFragment fragment = new DayFragment();
        fragment.day = day;
        fragment.selectedExercises = selectedExercises;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day, container, false);
        TextView textView = view.findViewById(R.id.day_text_view);
        exercisesRecyclerView = view.findViewById(R.id.exercises_recycler_view);
        exercisesRecyclerView.setNestedScrollingEnabled(false);
        selectedExercisesRecyclerView = view.findViewById(R.id.selected_exercises_recycler_view);
        selectedExercisesRecyclerView.setNestedScrollingEnabled(false);
        muscleGroupChipGroup = view.findViewById(R.id.muscle_group_chip_group);

        if (getArguments() != null) {
            int day = getArguments().getInt(ARG_DAY);
            String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);
            textView.setText(daysOfWeek[day - 1]);
        }

        exercisesAdapter = new ExerciseAdapter(new ArrayList<>(), this::onExerciseSelected);
        selectedExercisesAdapter = new ExerciseAdapter(selectedExercises, this::onExerciseDeselected);

        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesRecyclerView.setAdapter(exercisesAdapter);

        selectedExercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        selectedExercisesRecyclerView.setAdapter(selectedExercisesAdapter);

        muscleGroupChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                muscleGroup = chip.getText().toString().toLowerCase();
                loadExercisesFromFirestore();
            }
        });

        return view;
    }

    /**
     * Map of correspondences between the names of muscle groups in Spanish and English.
     * This map is used to get the English name of the selected muscle group,
     * which is used to fetch the corresponding exercises from Firestore.
     */
    private static final Map<String, String> MUSCLE_GROUP_MAP = new HashMap<String, String>() {{
        put("pecho", "chest");
        put("espalda", "back");
        put("piernas", "legs");
        put("hombros", "shoulders");
        put("brazos", "arms");
        put("abdominales", "abs");
    }};

    /**
     * Loads exercises from Firestore based on the selected muscle group and updates the UI.
     */
    private void loadExercisesFromFirestore() {
        // Get the current locale
        String locale = getResources().getConfiguration().locale.getLanguage();

        // Get the English name of the muscle group
        String muscleGroupEnglish = MUSCLE_GROUP_MAP.get(muscleGroup);

        // Append "_es" to the muscle group if the locale is Spanish
        String documentName = "es".equals(locale) ? muscleGroupEnglish + "_es" : muscleGroupEnglish;

        db.collection("muscleGroup").document(documentName).collection("exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Exercise> exercises = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");
                            String muscleGroup = document.getString("muscleGroup");
                            exercises.add(new Exercise(name, description, imageUrl, muscleGroup));
                        }
                        exercisesAdapter.updateExercises(exercises);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Callback method invoked when an exercise is selected.
     *
     * @param exercise The selected exercise.
     */
    public void onExerciseSelected(Exercise exercise) {
        ((NewRoutinesFragment) getParentFragment()).onExerciseSelected(day, exercise);
        selectedExercisesAdapter.notifyDataSetChanged();
    }

    /**
     * Callback method invoked when an exercise is deselected.
     *
     * @param exercise The deselected exercise.
     */
    public void onExerciseDeselected(Exercise exercise) {
        ((NewRoutinesFragment) getParentFragment()).onExerciseDeselected(day, exercise);
        selectedExercisesAdapter.notifyDataSetChanged();
    }
}
