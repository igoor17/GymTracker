package es.eduardo.gymtracker.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.eduardo.gymtracker.R;
public class ProgressFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userEmail;
    private Spinner monthSpinner;
    private LinearLayout progressLayout;
    private TextView totalChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress, container, false);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userEmail = auth.getCurrentUser().getEmail();

        monthSpinner = view.findViewById(R.id.month_spinner);
        progressLayout = view.findViewById(R.id.progress_layout);
        totalChange = view.findViewById(R.id.total_change);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = (String) parent.getItemAtPosition(position);
                updateProgress(selectedMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        return view;
    }

    private void updateProgress(String month) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Double[] weights = new Double[2]; // Array to hold initial and final weights
        for (int i = 1; i <= 4; i++) {
            final int weekNumber = i; // Variable final that copies the value of i
            String week = String.format(Locale.getDefault(), "%d-%s-W%d", year, month, weekNumber);
            db.collection("users").document(userEmail).collection(month).document(week)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String weightString = document.getString("weight");
                                String bmiString = document.getString("bmi");
                                if (weightString != null && bmiString != null) {
                                    try {
                                        double weight = Double.parseDouble(weightString);
                                        double bmi = Double.parseDouble(bmiString);
                                        // Now you can use weight and bmi as Double
                                        // Here you can create a new TextView for each week and add it to the LinearLayout
                                        TextView weekTextView = new TextView(getContext());
                                        weekTextView.setText(String.format(Locale.getDefault(), "Week %d: Weight = %.2f, BMI = %.2f", weekNumber, weight, bmi));
                                        weekTextView.setTextColor(getResources().getColor(R.color.white));
                                        progressLayout.addView(weekTextView);

                                        // Store the initial and final weight
                                        if (weights[0] == null) {
                                            weights[0] = weight;
                                        }
                                        weights[1] = weight;

                                    } catch (NumberFormatException e) {
                                        // weightString or bmiString cannot be converted to Double
                                    }
                                }
                            } else {
                                // The document does not exist
                            }
                        } else {
                            // There was an error getting the document
                        }
                    });
        }

        // Calculate and display the weight change
        if (weights[0] != null && weights[1] != null) {
            double weightChange = weights[1] - weights[0];
            totalChange.setText(String.format(Locale.getDefault(), "Total weight change: %.2f", weightChange));
        }
    }
}