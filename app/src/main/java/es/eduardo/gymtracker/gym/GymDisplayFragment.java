package es.eduardo.gymtracker.gym;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import es.eduardo.gymtracker.R;

public class GymDisplayFragment extends Fragment {

    FirebaseFirestore db;

    private TextView nameTextView;
    private TextView addressTextView;
    private TextView phoneNumberTextView;
    private Button deleteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gym_display, container, false);

        nameTextView = view.findViewById(R.id.name_text_view);
        addressTextView = view.findViewById(R.id.address);
        phoneNumberTextView = view.findViewById(R.id.phone_number);
        deleteButton = view.findViewById(R.id.remove_button);

        Bundle args = getArguments();
        if (args != null) {
            String name = args.getString("name", "No disponible");
            String address = args.getString("address", "No disponible");
            String phoneNumber = args.getString("phoneNumber", "No disponible");

            nameTextView.setText(name);
            addressTextView.setText(address);
            phoneNumberTextView.setText(phoneNumber);

            onDeleteButton(name);
        }



        return view;
    }

    private void onDeleteButton(String gymName) {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                db.collection("users").document(userEmail).collection("favorites").document(gymName)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                getActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error al eliminar el gimnasio de favoritos", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}