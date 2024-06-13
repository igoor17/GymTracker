package es.eduardo.gymtracker.routines;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.Exercise;

/**
 * Fragment for creating and saving new workout routines.
 */
public class NewRoutinesFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Variables
    private HashMap<Integer, List<Exercise>> selectedExercisesPerDay = new HashMap<>();

    // UI
    private ChipGroup daysChipGroup; // ChipGroup for selecting days of the week
    private Button saveButton; // Button to save the routine
    private EditText routineNameEditText; // EditText for entering routine name

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_routines, container, false);

        daysChipGroup = view.findViewById(R.id.days_chip_group);
        saveButton = view.findViewById(R.id.save_button);
        routineNameEditText = view.findViewById(R.id.routine_name);

        daysChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int day = group.indexOfChild(group.findViewById(checkedId)) + 1;
            onDaySelected(day);
        });

        saveButton.setOnClickListener(v -> onSaveButtonClicked());

        return view;
    }

    /**
     * Handles the selection of a day and updates the UI to show exercises for that day.
     *
     * @param day The selected day of the week (1-7, where 1 is Monday).
     */
    public void onDaySelected(int day) {
        List<Exercise> selectedExercises = selectedExercisesPerDay.get(day);
        if (selectedExercises == null) {
            selectedExercises = new ArrayList<>();
            selectedExercisesPerDay.put(day, selectedExercises);
        }

        DayFragment dayFragment = DayFragment.newInstance(day, selectedExercises);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.day_fragment_container, dayFragment);
        fragmentTransaction.commit();
    }

    /**
     * Handles the selection of an exercise on a specific day.
     *
     * @param day      The day of the week (1-7) on which the exercise is selected.
     * @param exercise The exercise that was selected.
     */
    public void onExerciseSelected(int day, Exercise exercise) {
        List<Exercise> selectedExercises = selectedExercisesPerDay.get(day);
        if (!selectedExercises.contains(exercise)) {
            selectedExercises.add(exercise);
        }
    }

    /**
     * Handles the deselection of an exercise on a specific day.
     *
     * @param day      The day of the week (1-7) on which the exercise is deselected.
     * @param exercise The exercise that was deselected.
     */
    public void onExerciseDeselected(int day, Exercise exercise) {
        List<Exercise> selectedExercises = selectedExercisesPerDay.get(day);
        selectedExercises.remove(exercise);
    }

    /**
     * Handles the click event when the save button is clicked to save the routine and its exercises.
     */
    public void onSaveButtonClicked() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String routineName = routineNameEditText.getText().toString();

        // Get the array with the names of the days of the week in English from resources
        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        // Get a reference to the "rutinas" folder in Firebase Storage
        StorageReference routinesFolderRef = FirebaseStorage.getInstance().getReference().child("rutinas");

        routinesFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // Get a list of all photos in the "rutinas" folder
                    List<StorageReference> photos = listResult.getItems();

                    // Select a random photo from the list
                    int randomIndex = new Random().nextInt(photos.size());
                    StorageReference randomPhotoRef = photos.get(randomIndex);

                    // Get the download URL of the random photo
                    randomPhotoRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String photoDownloadUrl = uri.toString();

                                // Count the total number of exercises
                                int daysWithExercises = 0;
                                int totalExercises = 0;
                                for (List<Exercise> exercises : selectedExercisesPerDay.values()) {
                                    if (!exercises.isEmpty()) {
                                        daysWithExercises++;
                                        totalExercises += exercises.size();
                                    }
                                }

                                // Create a map to store routine details
                                Map<String, Object> routineDetails = new HashMap<>();
                                routineDetails.put("imageUrl", photoDownloadUrl);
                                routineDetails.put("days", daysWithExercises); // Number of days
                                routineDetails.put("exercises", totalExercises); // Total number of exercises

                                // Save routine details to Firestore
                                db.collection("users").document(userEmail)
                                        .collection("routines").document(routineName)
                                        .set(routineDetails, SetOptions.merge());

                                // Save exercise details to Firestore
                                for (Map.Entry<Integer, List<Exercise>> entry : selectedExercisesPerDay.entrySet()) {
                                    int day = entry.getKey();
                                    List<Exercise> selectedExercises = entry.getValue();

                                    String dayName = daysOfWeek[day - 1];

                                    for (Exercise exercise : selectedExercises) {
                                        // Create a map to store exercise details
                                        Map<String, Object> exerciseDetails = new HashMap<>();
                                        exerciseDetails.put("name", exercise.getName());
                                        exerciseDetails.put("weight", 0); // You can update these values later
                                        exerciseDetails.put("reps", 0); // You can update these values later

                                        // Save exercise details to Firestore
                                        db.collection("users").document(userEmail)
                                                .collection("routines").document(routineName)
                                                .collection("exercises").document(dayName)
                                                .collection("exercises").document(exercise.getName())
                                                .set(exerciseDetails);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error getting download url", e));
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error listing files", e));
    }
}
