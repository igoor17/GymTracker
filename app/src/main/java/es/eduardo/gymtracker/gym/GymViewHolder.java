package es.eduardo.gymtracker.gym;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import es.eduardo.gymtracker.R;

/**
 * ViewHolder for the Gym RecyclerView.
 */
public class GymViewHolder extends RecyclerView.ViewHolder {

    /**
     * TextView to display the gym name.
     */
    TextView name;

    /**
     * Constructor to initialize the ViewHolder.
     *
     * @param itemView The View object that represents each item in the RecyclerView.
     */
    public GymViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.gym_name);
    }
}
