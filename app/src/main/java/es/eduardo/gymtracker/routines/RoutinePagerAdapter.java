package es.eduardo.gymtracker.routines;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class RoutinePagerAdapter extends FragmentStateAdapter {
    private List<Routine> routines;

    public RoutinePagerAdapter(@NonNull Fragment fragment, List<Routine> routines) {
        super(fragment);
        this.routines = routines;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new RoutineFragment(routines.get(position));
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }
}