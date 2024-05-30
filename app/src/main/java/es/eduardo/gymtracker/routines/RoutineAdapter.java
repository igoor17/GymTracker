package es.eduardo.gymtracker.routines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.function.Consumer;

import es.eduardo.gymtracker.routines.RoutineDisplayFragment;
import es.eduardo.gymtracker.R;

public class RoutineAdapter extends FragmentStateAdapter {
    private FragmentActivity context;
    private List<Routine> routines;
    private Consumer<Routine> onRoutineClicked;

    public RoutineAdapter(@NonNull FragmentActivity context, List<Routine> routines, Consumer<Routine> onRoutineClicked) {
        super(context);
        this.context = context;
        this.routines = routines;
        this.onRoutineClicked = onRoutineClicked;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Routine routine = routines.get(position);
        RoutineFragment routineFragment = new RoutineFragment(routine);
        routineFragment.setOnClickListener(v -> onRoutineClicked.accept(routine));
        return routineFragment;
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }
}