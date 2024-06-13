package es.eduardo.gymtracker.routines;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;
import java.util.function.Consumer;

import es.eduardo.gymtracker.R;

/**
 * Adapter for managing routines in a ViewPager2.
 */
public class RoutineAdapter extends FragmentStateAdapter {
    private FragmentActivity context;          // Activity context
    private List<Routine> routines;            // List of routines to display
    private Consumer<Routine> onRoutineClicked; // Callback when a routine is clicked

    /**
     * Constructor for the RoutineAdapter.
     *
     * @param context         The FragmentActivity context.
     * @param routines        List of routines to display.
     * @param onRoutineClicked Callback to handle when a routine is clicked.
     */
    public RoutineAdapter(@NonNull FragmentActivity context, List<Routine> routines, Consumer<Routine> onRoutineClicked) {
        super(context);
        this.context = context;
        this.routines = routines;
        this.onRoutineClicked = onRoutineClicked;
    }

    /**
     * Creates a new RoutineFragment for the given position.
     *
     * @param position Position of the routine in the list.
     * @return A new RoutineFragment instance.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Routine routine = routines.get(position);
        RoutineFragment routineFragment = new RoutineFragment(routine);
        routineFragment.setOnClickListener(v -> onRoutineClicked.accept(routine));
        return routineFragment;
    }

    /**
     * Returns the total number of routines in the adapter.
     *
     * @return The total number of routines.
     */
    @Override
    public int getItemCount() {
        return routines.size();
    }
}
