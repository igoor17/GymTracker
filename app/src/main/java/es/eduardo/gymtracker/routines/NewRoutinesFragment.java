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

public class NewRoutinesFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private HashMap<Integer, List<Exercise>> selectedExercisesPerDay = new HashMap<>();
    private ChipGroup daysChipGroup;
    private Button saveButton;
    private EditText routineNameEditText;

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

    public void onExerciseSelected(int day, Exercise exercise) {
        List<Exercise> selectedExercises = selectedExercisesPerDay.get(day);
        if (!selectedExercises.contains(exercise)) {
            selectedExercises.add(exercise);
        }
    }

    public void onExerciseDeselected(int day, Exercise exercise) {
        List<Exercise> selectedExercises = selectedExercisesPerDay.get(day);
        selectedExercises.remove(exercise);
    }

    public void onSaveButtonClicked() {
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String routineName = routineNameEditText.getText().toString();
        String[] daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        // Obtener una referencia a la carpeta "rutinas" en el almacenamiento de Firebase
        StorageReference routinesFolderRef = FirebaseStorage.getInstance().getReference().child("rutinas");

        routinesFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    // Obtener una lista de todas las fotos en la carpeta "rutinas"
                    List<StorageReference> photos = listResult.getItems();

                    // Seleccionar una foto aleatoria de la lista
                    int randomIndex = new Random().nextInt(photos.size());
                    StorageReference randomPhotoRef = photos.get(randomIndex);

                    // Obtener la URL de descarga de la foto aleatoria
                    randomPhotoRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                String photoDownloadUrl = uri.toString();

                                // Guardar los detalles de la rutina y la URL de descarga de la foto en Firestore
                                for (Map.Entry<Integer, List<Exercise>> entry : selectedExercisesPerDay.entrySet()) {
                                    int day = entry.getKey();
                                    List<Exercise> selectedExercises = entry.getValue();

                                    for (int i = 0; i < selectedExercises.size(); i++) {
                                        Exercise exercise = selectedExercises.get(i);

                                        // Crear un mapa para guardar los detalles del ejercicio
                                        Map<String, Object> exerciseDetails = new HashMap<>();
                                        exerciseDetails.put("name", exercise.getName());
                                        exerciseDetails.put("weight", 0); // Puedes actualizar estos valores más tarde
                                        exerciseDetails.put("reps", 0); // Puedes actualizar estos valores más tarde

                                        // Guardar los detalles del ejercicio y la URL de descarga de la foto en Firestore
                                        db.collection("users").document(userEmail)
                                                .collection("routines").document(routineName)
                                                .collection("exercises").document(daysOfWeek[day - 1])
                                                .collection(exercise.getName())
                                                .add(exerciseDetails)
                                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully written!"))
                                                .addOnFailureListener(e -> Log.w("Firestore", "Error writing document", e));

                                        Map<String, Object> routineDetails = new HashMap<>();
                                        routineDetails.put("imageUrl", photoDownloadUrl);

                                        db.collection("users").document(userEmail)
                                                .collection("routines").document(routineName)
                                                .set(routineDetails, SetOptions.merge());
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error getting download url", e));
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error listing files", e));
    }
}