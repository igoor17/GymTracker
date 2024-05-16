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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.Utils;

/**
 * The type Exercises fragment.
 */
public class ExercisesFragment extends Fragment {

    // Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * The Exercises text view.
     */
    TextView exercisesTextView;
    /**
     * The Exercises recycler view.
     */
    RecyclerView exercisesRecyclerView;

    // Info Ejercicios
    private List<Exercise> exerciseNames = new ArrayList<>();
    private String muscleGroup;
    private String documentId;
    private String firestoreMuscleGroup;

    /**
     * The Exercise adapter.
     */
    ExerciseAdapter exerciseAdapter;

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     * This method inflates the layout for the fragment, initializes the UI elements,
     * retrieves the muscle group from the arguments passed to this fragment,
     * fetches the exercises from Firestore, and sets up the RecyclerView with the exercises.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_excercises, container, false);

        exercisesTextView = view.findViewById(R.id.exercises);
        exercisesRecyclerView = view.findViewById(R.id.exercisesRecyclerView);
        exercisesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            String language = getResources().getConfiguration().locale.getLanguage();
            String exercisesText;
            muscleGroup = getArguments().getString("muscleGroup");

            firestoreMuscleGroup = language.equals("es") ? muscleGroup + "_es" : muscleGroup;

            if (language.equals("es")) { // Si el idioma es español
                exercisesText = getString(R.string.exercises) + " de " + Utils.getTranslatedMuscleGroup(muscleGroup,getActivity());
            } else { // Si el idioma es inglés
                exercisesText = Utils.getTranslatedMuscleGroup(muscleGroup,getActivity()) + " " + getString(R.string.exercises);
            }

            exercisesTextView.setText(exercisesText);
        }

        db.collection("muscleGroup").document(firestoreMuscleGroup).collection("exercises")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        exerciseNames.clear(); // Limpia la lista antes de agregar elementos
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String description = document.getString("description");
                            String imageUrl = document.getString("imageUrl");
                            exerciseNames.add(new Exercise(name, description, imageUrl, muscleGroup));
                            documentId = document.getId();
                        }
                        exerciseAdapter = new ExerciseAdapter(exerciseNames, exercise -> {
                            ExerciseDisplayFragment exerciseDisplayFragment = new ExerciseDisplayFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("name", exercise.getName());
                            bundle.putString("description", exercise.getDescription());
                            bundle.putString("muscleGroup", exercise.getMuscleGroup());
                            exerciseDisplayFragment.setArguments(bundle);

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, exerciseDisplayFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        });
                        exercisesRecyclerView.setAdapter(exerciseAdapter);
                    }
                });

        return view;
    }
}