package es.eduardo.gymtracker.store;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

import es.eduardo.gymtracker.R;


public class StoreFragment extends Fragment {

    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Productos
    ImageView proteinImageView;
    ImageView preworkoutImageView;
    ImageView creatineImageView;
    ImageView strapsImageView;
    ImageView shakeImageView;
    ImageView towelImageView;
    ImageView wristbandImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        proteinImageView = view.findViewById(R.id.protein);
        preworkoutImageView = view.findViewById(R.id.preworkout);
        creatineImageView = view.findViewById(R.id.creatine);
        strapsImageView = view.findViewById(R.id.straps);
        shakeImageView = view.findViewById(R.id.shakers);
        towelImageView = view.findViewById(R.id.towels);
        wristbandImageView = view.findViewById(R.id.wristbands);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProductDisplayFragment();
                Bundle bundle = new Bundle();
                int id = v.getId();
                if (id == R.id.protein) {
                    bundle.putString("category", getString(R.string.category_nutrition));
                    bundle.putString("product", getString(R.string.protein));
                } else if (id == R.id.preworkout) {
                    bundle.putString("category", getString(R.string.category_nutrition));
                    bundle.putString("product", getString(R.string.preworkout));
                } else if (id == R.id.creatine) {
                    bundle.putString("category", getString(R.string.category_nutrition));
                    bundle.putString("product", getString(R.string.creatine));
                } else if (id == R.id.straps) {
                    bundle.putString("category", getString(R.string.category_accessories));
                    bundle.putString("product", getString(R.string.straps));
                } else if (id == R.id.shakers) {
                    bundle.putString("category", getString(R.string.category_accessories));
                    bundle.putString("product", getString(R.string.shakers));
                } else if (id == R.id.towels) {
                    bundle.putString("category", getString(R.string.category_accessories));
                    bundle.putString("product", getString(R.string.towels));
                } else if (id == R.id.wristbands) {
                    bundle.putString("category", getString(R.string.category_accessories));
                    bundle.putString("product", getString(R.string.wristbands));
                }

                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };

        proteinImageView.setOnClickListener(onClickListener);
        preworkoutImageView.setOnClickListener(onClickListener);
        creatineImageView.setOnClickListener(onClickListener);
        strapsImageView.setOnClickListener(onClickListener);
        shakeImageView.setOnClickListener(onClickListener);
        towelImageView.setOnClickListener(onClickListener);
        wristbandImageView.setOnClickListener(onClickListener);


        return view;
    }
}