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

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.Exercise;
import es.eduardo.gymtracker.exercises.ExerciseAdapter;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DayFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Argumentos
    public static final String ARG_DAY = "day"; // Clave utilizada para pasar el día como argumento entre fragmentos

    // Variables
    private int day; // Día de la semana para el que se está creando la rutina
    private List<Exercise> selectedExercises; // Lista de ejercicios seleccionados para el día especificado
    private String muscleGroup; // Grupo muscular seleccionado para filtrar los ejercicios

    // UI
    private RecyclerView exercisesRecyclerView; // Lista de ejercicios disponibles
    private RecyclerView selectedExercisesRecyclerView; // Lista de ejercicios seleccionados
    private ChipGroup muscleGroupChipGroup; // Botones de grupos musculares

    // Adapters
    private ExerciseAdapter exercisesAdapter;
    private ExerciseAdapter selectedExercisesAdapter;


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

    private void loadExercisesFromFirestore() {
        db.collection("muscleGroup").document(muscleGroup).collection("exercises")
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

    public void onExerciseSelected(Exercise exercise) {
        ((NewRoutinesFragment) getParentFragment()).onExerciseSelected(day, exercise);
        selectedExercisesAdapter.notifyDataSetChanged();
    }

    public void onExerciseDeselected(Exercise exercise) {
        ((NewRoutinesFragment) getParentFragment()).onExerciseDeselected(day, exercise);
        selectedExercisesAdapter.notifyDataSetChanged();
    }
}