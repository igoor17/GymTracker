package es.eduardo.gymtracker.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.function.Consumer;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.exercises.ExerciseDisplayFragment;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private Consumer<Exercise> onExerciseClicked;

    public ExerciseAdapter(List<Exercise> exercises, Consumer<Exercise> onExerciseClicked) {
        this.exercises = exercises;
        this.onExerciseClicked = onExerciseClicked;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, onExerciseClicked);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    public void updateExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView backgroundImageView;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            backgroundImageView = itemView.findViewById(R.id.background_image_view);
        }

        public void bind(Exercise exercise, Consumer<Exercise> onExerciseClicked) {
            nameTextView.setText(exercise.getName());
            Glide.with(itemView)
                    .load(exercise.getImageUrl())
                    .into(backgroundImageView);
            itemView.setOnClickListener(v -> onExerciseClicked.accept(exercise));

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Crear un nuevo fragmento de visualización de ejercicio
                    ExerciseDisplayFragment exerciseDisplayFragment = new ExerciseDisplayFragment();

                    // Crear un nuevo Bundle para pasar los datos del ejercicio al fragmento
                    Bundle bundle = new Bundle();
                    bundle.putString("name", exercise.getName());
                    bundle.putString("description", exercise.getDescription());
                    bundle.putString("muscleGroup", exercise.getMuscleGroup());

                    // Pasar el Bundle al fragmento
                    exerciseDisplayFragment.setArguments(bundle);

                    // Abrir el fragmento
                    FragmentManager fragmentManager = ((FragmentActivity) v.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, exerciseDisplayFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    return true;
                }
            });
        }
    }
}