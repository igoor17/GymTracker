package es.eduardo.gymtracker.gym;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import es.eduardo.gymtracker.R;

public class GymViewHolder extends RecyclerView.ViewHolder {
    TextView name;

    GymViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.gym_name);
    }
}