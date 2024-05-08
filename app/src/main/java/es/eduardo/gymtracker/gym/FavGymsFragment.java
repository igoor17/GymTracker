package es.eduardo.gymtracker.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.gymtracker.R;

public class FavGymsFragment extends Fragment {

    private RecyclerView favGymsRecyclerView;
    private FirebaseFirestore db;
    private String userEmail;
    private List<Gym> gyms;
    private GymAdapter gymAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_gyms, container, false);

        favGymsRecyclerView = view.findViewById(R.id.fav_gyms_recycler_view);
        favGymsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        gyms = new ArrayList<>();
        gymAdapter = new GymAdapter(gyms, getChildFragmentManager());
        favGymsRecyclerView.setAdapter(gymAdapter);

        db.collection("users").document(userEmail).collection("favorites")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String name = document.getString("nombre");
                            String address = document.getString("address");
                            String phoneNumber = document.getString("phoneNumber");
                            double lat= document.getDouble("lat");
                            double lon= document.getDouble("lon");

                            if (name == null) name = "No disponible";
                            if (address == null) address = "No disponible";
                            if (phoneNumber == null) phoneNumber = "No disponible";

                            gyms.add(new Gym(name, address, phoneNumber,lat,lon));
                        }
                        gymAdapter.notifyDataSetChanged();
                    } else {
                        // Hubo un error al obtener los documentos
                    }
                });

        return view;
    }
}