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
import java.util.Map;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.Exercise;
import es.eduardo.gymtracker.exercises.ExerciseAdapter;
import es.eduardo.gymtracker.exercises.ExerciseDisplayFragment;

public class RoutineDisplayFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String routineName;
    private RecyclerView exercisesRecyclerView;
    private ExerciseAdapter exerciseAdapter;
    private TextView noExercisesTextView;

    // Mapeo de los números de los chips a los nombres de los días de la semana en inglés
    private String[] daysOfWeek;

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

        // Obtener los días de la semana del string-array en los recursos
        daysOfWeek = getResources().getStringArray(R.array.days_of_week);

        exerciseAdapter = new ExerciseAdapter(new ArrayList<>(), this::onExerciseSelected);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        exercisesRecyclerView.setAdapter(exerciseAdapter);

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = group.findViewById(checkedId);
            if (chip != null) {
                // Obtén el número del día de la semana del texto del chip y úsalo para obtener el nombre del día de la semana en inglés
                int dayOfWeekNumber = Integer.parseInt(chip.getText().toString()) - 1;
                String dayOfWeek = daysOfWeek[dayOfWeekNumber];
                Log.d("RoutineDisplayFragment", "Day: " + dayOfWeek);
                loadExercisesForDay(dayOfWeek);
            }
        });

        return view;
    }

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

    public void onExerciseSelected(Exercise exercise) {
        // Crear una nueva instancia del fragmento de detalles del ejercicio
        ExerciseDisplayFragment exerciseDisplayFragment = new ExerciseDisplayFragment();

        // Crear un nuevo Bundle para pasar el ejercicio seleccionado al fragmento de detalles del ejercicio
        Bundle bundle = new Bundle();
        bundle.putSerializable("selected_exercise", exercise);
        exerciseDisplayFragment.setArguments(bundle);

        // Navegar al fragmento de detalles del ejercicio
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, exerciseDisplayFragment)
                    .addToBackStack(null)
                    .commit();
        }

    }
}