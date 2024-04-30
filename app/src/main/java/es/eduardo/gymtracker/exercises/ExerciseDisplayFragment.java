package es.eduardo.gymtracker.exercises;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

import java.util.Objects;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.Utils;

public class ExerciseDisplayFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView exerciseNameTextView;
    TextView exerciseDescriptionTextView;
    TextView exerciseGroupTextView;
    VideoView exerciseVideoView;
    ScrollView scrollView;

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

        exerciseNameTextView = view.findViewById(R.id.exerciseName);
        exerciseDescriptionTextView = view.findViewById(R.id.exerciseDescription);
        exerciseGroupTextView = view.findViewById(R.id.exerciseGroup);
        exerciseVideoView = view.findViewById(R.id.videoView);
        scrollView = view.findViewById(R.id.scrollView);

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
    }

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

                        exerciseVideoView.setVideoURI(Uri.parse(videoUrl));

                        MediaController mediaController = new MediaController(getContext());
                        mediaController.setAnchorView(exerciseVideoView);
                        exerciseVideoView.setMediaController(mediaController);

                        exerciseVideoView.start();

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