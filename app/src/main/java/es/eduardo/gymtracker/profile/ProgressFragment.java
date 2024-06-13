package es.eduardo.gymtracker.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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

import java.time.Month;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.MonthAdapter;

/**
 * Fragment to display user progress by weeks within a specific month.
 */
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

        String[] monthItems = getResources().getStringArray(R.array.months);

        MonthAdapter monthAdapter = new MonthAdapter(getContext(), Arrays.asList(monthItems));

        monthSpinner.setAdapter(monthAdapter);

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

    /**
     * Update the UI with user's weekly progress for the selected month.
     *
     * @param month The selected month to display progress (e.g., "January", "February").
     */
    private void updateProgress(String month) {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        Double[] weights = new Double[2]; // Array to hold initial and final weights
        TextView[] weekTextViews = new TextView[4]; // Array to hold the TextViews for the weeks
        progressLayout.removeAllViews(); // Remove all previous TextViews
        for (int i = 1; i <= 4; i++) {
            final int weekNumber = i; // Final variable copying the value of i
            String monthNameUpper = month.toUpperCase(Locale.getDefault());
            Month monthEnum = Month.valueOf(monthNameUpper);
            int monthNumber = monthEnum.getValue();
            String monthNumberStr = String.format(Locale.getDefault(), "%02d", monthNumber);
            String week = String.format(Locale.getDefault(), "%d-%s-W%d", year, monthNumberStr, weekNumber);

            Log.println(Log.INFO, "ProgressFragment", "Week: " + week);

            db.collection("users").document(userEmail).collection(month).document(week)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String weightString = document.getString("weight");
                                long bmiLong = document.getLong("bmi").intValue();
                                String bmiString = String.valueOf(bmiLong);
                                if (weightString != null && bmiString != null) {
                                    try {
                                        double weight = Double.parseDouble(weightString);
                                        double bmi = Double.parseDouble(bmiString);
                                        // Create a new TextView for the week and add it to the weekTextViews array
                                        TextView weekTextView = new TextView(getContext());
                                        weekTextView.setText(String.format(Locale.getDefault(), "Week %d: Weight = %.2f Kg, BMI = %.2f Kg/m2", weekNumber, weight, bmi));
                                        weekTextView.setTextColor(getResources().getColor(R.color.white));
                                        weekTextViews[weekNumber - 1] = weekTextView;

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

                        // Check if all the TextViews have been created
                        if (!Arrays.asList(weekTextViews).contains(null)) {
                            // All the TextViews have been created, so add them to the LinearLayout
                            for (TextView weekTextView : weekTextViews) {
                                progressLayout.addView(weekTextView);
                            }

                            // Calculate and display the weight change
                            if (weights[0] != null && weights[1] != null) {
                                double weightChange = weights[1] - weights[0];
                                char sign = weightChange > weights[0] ? '+' : '-';
                                totalChange.setText(String.format(Locale.getDefault(), "Total change: %c%.2f Kg", sign, weightChange));
                            }
                        }
                    });
        }
    }
}
