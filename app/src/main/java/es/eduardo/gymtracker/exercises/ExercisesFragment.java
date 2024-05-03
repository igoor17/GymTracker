package es.eduardo.gymtracker.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.Utils;

public class ExercisesFragment extends Fragment {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Info Ejercicios
    private List<String> exerciseNames = new ArrayList<>();
    private String muscleGroup;
    private String documentId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_excercises, container, false);

        TextView exercisesTextView = view.findViewById(R.id.exercises);
        GridView exercisesGridView = view.findViewById(R.id.exercisesGridView);

        if (getArguments() != null) {
            String language = getResources().getConfiguration().locale.getLanguage();
            String exercisesText;
            muscleGroup = getArguments().getString("muscleGroup");

            if (language.equals("es")) { // Si el idioma es español
                exercisesText = getString(R.string.exercises) + " de " + Utils.getTranslatedMuscleGroup(muscleGroup,getActivity());
            } else { // Si el idioma es inglés
                exercisesText = Utils.getTranslatedMuscleGroup(muscleGroup,getActivity()) + " " + getString(R.string.exercises);
            }

            exercisesTextView.setText(exercisesText);
        }

        db.collection("muscleGroup").document(muscleGroup).collection("exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        exerciseNames.clear(); // Limpia la lista antes de agregar elementos
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            exerciseNames.add(document.getString("name"));
                            documentId = document.getId();

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_exercise, exerciseNames);
                        exercisesGridView.setAdapter(adapter);
                    }
                });

        exercisesGridView.setOnItemClickListener((parent, view1, position, id) -> {
            String exerciseName = exerciseNames.get(position);
            db.collection("muscleGroup").document(muscleGroup).collection("exercises").document(documentId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String description = documentSnapshot.getString("description");

                        ExerciseDisplayFragment exerciseDisplayFragment = new ExerciseDisplayFragment();

                        Bundle bundle = new Bundle();
                        bundle.putString("name", exerciseName);
                        bundle.putString("description", description);
                        bundle.putString("muscleGroup", muscleGroup);
                        exerciseDisplayFragment.setArguments(bundle);

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, exerciseDisplayFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    });
        });

        return view;
    }
}