package es.eduardo.gymtracker.exercises;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.Utils;

/**
 * The type Exercise display fragment.
 */
public class ExerciseDisplayFragment extends Fragment {

    /**
     * The Db.
     */
// Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * The Exercise name text view.
     */
// Info Ejercicio UI
    TextView exerciseNameTextView;
    /**
     * The Exercise description text view.
     */
    TextView exerciseDescriptionTextView;
    /**
     * The Exercise group text view.
     */
    TextView exerciseGroupTextView;
    /**
     * The Exercise video view.
     */
    VideoView exerciseVideoView;

    /**
     * The Scroll view.
     */
// UI
    ScrollView scrollView;
    /**
     * The Back button.
     */
    ImageButton backButton;

    // Info Ejercicio
    private String exerciseName;
    private String exerciseDescription;
    private String exerciseGroup;


    /**
     * Called when the fragment is starting. This is where most initialization should go.
     * Retrieves the exercise name, description, and muscle group from the arguments passed to this fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseName = getArguments().getString("name");
            exerciseDescription = getArguments().getString("description");
            exerciseGroup = getArguments().getString("muscleGroup");

        }
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_display, container, false);
    }


    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created.
     * The fragment's view hierarchy is not however attached to its parent at this point.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        exerciseNameTextView = view.findViewById(R.id.exerciseName);
        exerciseDescriptionTextView = view.findViewById(R.id.exerciseDescription);
        exerciseGroupTextView = view.findViewById(R.id.exerciseGroup);
        exerciseVideoView = view.findViewById(R.id.videoView);
        scrollView = view.findViewById(R.id.scrollView);
        backButton = view.findViewById(R.id.backButton);

        String translatedMuscleGroup = Utils.getTranslatedMuscleGroup(exerciseGroup,getActivity());
        exerciseNameTextView.setText(exerciseName);
        exerciseDescriptionTextView.setText(exerciseDescription);
        exerciseGroupTextView.setText(translatedMuscleGroup);
        getExerciseVideo();

        // Ajusta el margen inferior del ScrollView para que no se superponga con la barra de acción
        // Obtén la altura de la barra de acción
        int actionBarSize = 0;
        TypedValue typedValue = new TypedValue();
        if (requireContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            actionBarSize = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }

        // Añade 25dp al margen inferior
        float additionalMargin = 25 * getResources().getDisplayMetrics().density;
        int finalMargin = Math.round(actionBarSize + additionalMargin);

        // Establece el nuevo margen
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, finalMargin);
        scrollView.setLayoutParams(layoutParams);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    /**
     * Retrieves the video URL of the exercise from Firestore and sets up the VideoView to play the video.
     * If the video URL is null, logs a message and does not set up the VideoView.
     */
    private void getExerciseVideo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("muscleGroup").document(exerciseGroup).collection("exercises").document(exerciseName);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Obtiene la URL de descarga del video
                        String videoUrl = document.getString("videoUrl");

                        // Configura el VideoView y reproduce el video

                        if (videoUrl != null) {
                            // Set up the VideoView and play the video
                            exerciseVideoView.setVideoURI(Uri.parse(videoUrl));

                            MediaController mediaController = new MediaController(getContext());
                            mediaController.setAnchorView(exerciseVideoView);
                            exerciseVideoView.setMediaController(mediaController);

                            exerciseVideoView.start();
                        } else {
                            Log.d("ExerciseDisplayFragment", "videoUrl is null");
                        }

                    } else {
                        Log.d("ExerciseDisplayFragment", "No such document");
                    }
                } else {
                    Log.d("ExerciseDisplayFragment", "get failed with ", task.getException());
                }
            }
        });
    }
}