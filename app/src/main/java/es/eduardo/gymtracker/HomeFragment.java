package es.eduardo.gymtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import es.eduardo.gymtracker.muscular_groups.ExcercisesFragment;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ImageView chestImageView = view.findViewById(R.id.chest);
        ImageView backImageView = view.findViewById(R.id.back);
        ImageView armsImageView = view.findViewById(R.id.arms);
        ImageView legsImageView = view.findViewById(R.id.legs);

        View.OnClickListener imageViewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment;
                Bundle bundle = new Bundle();
                int id = v.getId();
                if (id == R.id.chest) {
                    bundle.putString("muscleGroup", "chest");
                    fragment = new ExcercisesFragment();
                } else if (id == R.id.back) {
                    bundle.putString("muscleGroup", "back");
                    fragment = new ExcercisesFragment();
                } else if (id == R.id.arms) {
                    bundle.putString("muscleGroup", "arms");
                    fragment = new ExcercisesFragment();
                } else if (id == R.id.legs) {
                    bundle.putString("muscleGroup", "legs");
                    fragment = new ExcercisesFragment();
                } else if (id == R.id.abs) {
                    bundle.putString("muscleGroup", "abs");
                    fragment = new ExcercisesFragment();
                } else if (id == R.id.shoulders) {
                    bundle.putString("muscleGroup", "shoulders");
                    fragment = new ExcercisesFragment();
                } else {
                    throw new IllegalStateException("Unexpected value: " + id);
                }

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        chestImageView.setOnClickListener(imageViewClickListener);
        backImageView.setOnClickListener(imageViewClickListener);
        armsImageView.setOnClickListener(imageViewClickListener);
        legsImageView.setOnClickListener(imageViewClickListener);

        return view;
    }
}