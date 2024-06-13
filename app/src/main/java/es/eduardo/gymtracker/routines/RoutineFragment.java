package es.eduardo.gymtracker.routines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import es.eduardo.gymtracker.R;

/**
 * Fragment for displaying details of a routine.
 */
public class RoutineFragment extends Fragment {

    // Variables
    private Routine routine;
    private View.OnClickListener onClickListener;

    // UI components
    private ImageView routineImage;
    private TextView routineName;
    private TextView routineDays;
    private TextView routineExercises;

    /**
     * Constructor to initialize RoutineFragment with a specific routine.
     *
     * @param routine The routine to display.
     */
    public RoutineFragment(Routine routine) {
        this.routine = routine;
    }

    /**
     * Sets a click listener for the fragment.
     *
     * @param onClickListener Click listener to be set.
     */
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Inflates the layout for this fragment and initializes UI components.
     *
     * @param inflater           LayoutInflater to inflate the layout.
     * @param container          ViewGroup container for the fragment UI.
     * @param savedInstanceState Saved state of the fragment.
     * @return Inflated View of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        // Initialize UI components
        routineImage = view.findViewById(R.id.routine_image);
        routineName = view.findViewById(R.id.routine_name);
        routineDays = view.findViewById(R.id.routine_days);
        routineExercises = view.findViewById(R.id.routine_exercises);

        // Load routine image using Glide library
        Glide.with(this)
                .load(routine.getImageUrl())
                .into(routineImage);

        // Set routine name, days, and total exercises count
        routineName.setText(routine.getName());
        routineDays.setText(String.valueOf(routine.getDays() + " " + getString(R.string.days)));
        routineExercises.setText(String.valueOf(routine.getTotalExercises() + " " + getString(R.string.exercises)));

        // Set click listener if provided
        if (onClickListener != null) {
            view.setOnClickListener(onClickListener);
        }

        return view;
    }
}
