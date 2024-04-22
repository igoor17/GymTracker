package es.eduardo.gymtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.muscular_groups.ArmsFragment;
import es.eduardo.gymtracker.muscular_groups.BackFragment;
import es.eduardo.gymtracker.muscular_groups.ChestFragment;
import es.eduardo.gymtracker.muscular_groups.LegsFragment;


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
                int id = v.getId();
                if (id == R.id.chest) {
                    fragment = new ChestFragment();
                } else if (id == R.id.back) {
                    fragment = new BackFragment();
                } else if (id == R.id.arms) {
                    fragment = new ArmsFragment();
                } else if (id == R.id.legs) {
                    fragment = new LegsFragment();
                } else {
                    throw new IllegalStateException("Unexpected value: " + id);
                }

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