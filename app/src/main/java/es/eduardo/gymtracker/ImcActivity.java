package es.eduardo.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.eduardo.gymtracker.utils.Utils;

/**
 * Activity to calculate and display the Body Mass Index (BMI) of the logged-in user.
 */
public class ImcActivity extends AppCompatActivity {

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    // UI elements
    TextView ageTxt, conditionTxt, imcTxt, suggestionTxt;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imc);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ageTxt = findViewById(R.id.ageTxt);
        conditionTxt = findViewById(R.id.condition);
        imcTxt = findViewById(R.id.imcTxt);
        suggestionTxt = findViewById(R.id.suggestion);
        nextButton = findViewById(R.id.nextButton);

        calculateImc();
        redirectActivity();
    }

    /**
     * Calculates the Body Mass Index (BMI) of the user and updates the UI with the result.
     */
    private void calculateImc() {
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();

            db.collection("users").document(email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve user data
                        double height = Double.parseDouble(document.getString("height"));
                        double weight = Double.parseDouble(document.getString("weight"));
                        int age = Integer.parseInt(document.getString("age"));

                        // Convert height to meters
                        double heightInM = height / 100;
                        // Calculate BMI
                        double imc = weight / Math.pow(heightInM, 2);

                        // Save BMI to Firestore
                        db.collection("users").document(email)
                                .update("bmi", imc)
                                .addOnSuccessListener(aVoid -> {
                                    // Display results
                                    ageTxt.setText(String.valueOf(age));
                                    imcTxt.setText(String.format("%.2f", imc));
                                    conditionTxt.setText(Utils.getCategory((float) imc, this));
                                    suggestionTxt.setText(Utils.getSuggestions((float) imc, this));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ImcActivity.this, "Error saving BMI", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(ImcActivity.this, "User document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImcActivity.this, "Error getting user document", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(ImcActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Redirects to the MainActivity upon button click.
     */
    private void redirectActivity() {
        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(ImcActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

}
