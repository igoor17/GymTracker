package es.eduardo.gymtracker.routines;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import es.eduardo.gymtracker.R;

public class RoutineFragment extends Fragment {
    private Routine routine;

    private ImageView routineImage;
    private TextView routineName;
    private TextView routineDays;

    public RoutineFragment(Routine routine) {
        this.routine = routine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        routineImage = view.findViewById(R.id.routine_image);
        routineName = view.findViewById(R.id.routine_name);
        routineDays = view.findViewById(R.id.routine_days);

        Glide.with(this)
                .load(routine.getImageUrl())
                .into(routineImage);

        routineName.setText(routine.getName());
        routineDays.setText(String.valueOf(routine.getDays()));

        return view;
    }
}