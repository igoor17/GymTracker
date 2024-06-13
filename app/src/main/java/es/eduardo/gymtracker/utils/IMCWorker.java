package es.eduardo.gymtracker.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Worker class to calculate and store BMI and weight data in Firestore.
 */
public class IMCWorker extends Worker {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    /**
     * Constructor for IMCWorker.
     *
     * @param context      The application context.
     * @param workerParams Parameters to configure the worker.
     */
    public IMCWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    /**
     * This method is called to do the actual work of the worker.
     *
     * @return The result of the worker's computation.
     */
    @NonNull
    @Override
    public Result doWork() {
        String userEmail = auth.getCurrentUser().getEmail();

        // Retrieve BMI and weight data from Firebase
        db.collection("users").document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            double imc = document.getDouble("bmi");
                            String weight = document.getString("weight");

                            // Create a new document in the current month's collection with user's BMI and weight
                            String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().getTime());
                            String week = getWeekOfMonth();

                            Map<String, Object> data = new HashMap<>();
                            data.put("bmi", imc);
                            data.put("weight", weight);

                            db.collection("users").document(userEmail).collection(month).document(week)
                                    .set(data, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        // Document created successfully
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error creating document
                                    });
                        } else {
                            // Document doesn't exist
                        }
                    } else {
                        // Error getting document
                    }
                });

        return Result.success();
    }

    /**
     * Helper method to get the week of the month in the format "YYYY-MM-W#".
     *
     * @return String representing the week of the month.
     */
    private String getWeekOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return String.format(Locale.getDefault(), "%d-%02d-W%d", year, month, week);
    }
}
