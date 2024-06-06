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

public class IMCWorker extends Worker {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public IMCWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        String userEmail = auth.getCurrentUser().getEmail();

        // Recoger el IMC y el peso del usuario de Firebase
        db.collection("users").document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            double imc = document.getDouble("bmi");
                            String weight = document.getString("weight");

                            // Crear un nuevo documento en la colección del mes actual con el IMC y el peso del usuario
                            String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().getTime());
                            String week = getWeekOfMonth();

                            Map<String, Object> data = new HashMap<>();
                            data.put("bmi", imc);
                            data.put("weight", weight);

                            db.collection("users").document(userEmail).collection(month).document(week)
                                    .set(data, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        // El documento se creó correctamente
                                    })
                                    .addOnFailureListener(e -> {
                                        // Hubo un error al crear el documento
                                    });
                        } else {
                            // El documento no existe
                        }
                    } else {
                        // Hubo un error al obtener el documento
                    }
                });

        return Result.success();
    }

    private String getWeekOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        return String.format(Locale.getDefault(), "%d-%02d-W%d", year, month, week);
    }
}