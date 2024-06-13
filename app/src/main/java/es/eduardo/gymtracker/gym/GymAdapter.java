package es.eduardo.gymtracker.gym;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import es.eduardo.gymtracker.R;

/**
 * Adapter for displaying a list of Gym objects in a RecyclerView.
 */
public class GymAdapter extends RecyclerView.Adapter<GymViewHolder> {
    private List<Gym> gyms;
    private FragmentManager fragmentManager;

    /**
     * Constructs a new GymAdapter with the specified list of gyms and fragment manager.
     *
     * @param gyms             the list of Gym objects to be displayed
     * @param fragmentManager  the FragmentManager to handle fragment transactions
     */
    public GymAdapter(List<Gym> gyms, FragmentManager fragmentManager) {
        this.gyms = gyms;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Called when RecyclerView needs a new {@link GymViewHolder} of the given type to represent an item.
     *
     * @param parent   the ViewGroup into which the new View will be added
     * @param viewType the view type of the new View
     * @return a new GymViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gym_item, parent, false);
        return new GymViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   the GymViewHolder which should be updated to represent the contents of the item
     *                 at the given position in the data set
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(@NonNull GymViewHolder holder, int position) {
        Gym gym = gyms.get(position);
        holder.name.setText(gym.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GymDisplayFragment gymDisplayFragment = new GymDisplayFragment();

                Bundle bundle = new Bundle();
                bundle.putString("name", gym.getNombre());
                bundle.putString("address", gym.getAddress());
                bundle.putString("phoneNumber", gym.getPhoneNumber());

                gymDisplayFragment.setArguments(bundle);

                fragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, gymDisplayFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return the total number of items in this adapter
     */
    @Override
    public int getItemCount() {
        return gyms.size();
    }
}
