package es.eduardo.gymtracker.exercises;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import es.eduardo.gymtracker.R;

public class ExerciseDisplayFragment extends Fragment {

    private String exerciseName;
    private String exerciseDescription;
    private String exerciseGroup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseName = getArguments().getString("name");
            exerciseDescription = getArguments().getString("description");
            exerciseGroup = getArguments().getString("muscleGroup");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_display, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView exerciseNameTextView = view.findViewById(R.id.exerciseName);
        TextView exerciseDescriptionTextView = view.findViewById(R.id.exerciseDescription);
        TextView exerciseGroupTextView = view.findViewById(R.id.exerciseGroup);

        exerciseNameTextView.setText(exerciseName);
        exerciseDescriptionTextView.setText(exerciseDescription); // Establece el texto de la descripción del ejercicio
        exerciseGroupTextView.setText(exerciseGroup);
    }
}