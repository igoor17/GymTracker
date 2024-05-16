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

/**
 * The type Exercise adapter.
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<Exercise> exercises;
    private Consumer<Exercise> onExerciseClicked;

    /**
     * Instantiates a new Exercise adapter.
     *
     * @param exercises         the exercises
     * @param onExerciseClicked the on exercise clicked
     */
    public ExerciseAdapter(List<Exercise> exercises, Consumer<Exercise> onExerciseClicked) {
        this.exercises = exercises;
        this.onExerciseClicked = onExerciseClicked;
    }

    /**
     * Called when RecyclerView needs a new {@link ExerciseViewHolder} of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ExerciseViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should update the contents of the {@link ExerciseViewHolder#itemView} to reflect the item at the given position.
     *
     * @param holder The ExerciseViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.bind(exercise, onExerciseClicked);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Update exercises.
     *
     * @param exercises the exercises
     */
    public void updateExercises(List<Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    /**
     * The type Exercise view holder.
     */
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ImageView backgroundImageView;

        /**
         * Instantiates a new Exercise view holder.
         *
         * @param itemView the item view
         */
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            backgroundImageView = itemView.findViewById(R.id.background_image_view);
        }

        /**
         * Bind.
         *
         * @param exercise          the exercise
         * @param onExerciseClicked the on exercise clicked
         */
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