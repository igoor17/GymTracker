package es.eduardo.gymtracker.routines;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.eduardo.gymtracker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoutineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoutineFragment extends Fragment {
    private Routine routine;

    public RoutineFragment(Routine routine) {
        this.routine = routine;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_routine, container, false);
        // Aquí puedes inicializar tus vistas y mostrar los detalles de la rutina
        return view;
    }
}