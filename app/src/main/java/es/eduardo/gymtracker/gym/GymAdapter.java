package es.eduardo.gymtracker.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import es.eduardo.gymtracker.R;

public class GymAdapter extends RecyclerView.Adapter<GymViewHolder> {
    private List<Gym> gyms;
    private FragmentManager fragmentManager;

    public GymAdapter(List<Gym> gyms, FragmentManager fragmentManager) {
        this.gyms = gyms;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public GymViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gym_item, parent, false);
        return new GymViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return gyms.size();
    }
}