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

public class ImcActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    //UI
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

    private void calculateImc(){
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();

            db.collection("users").document(email).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Coger los datos del usuario
                        double height = Double.parseDouble(document.getString("height"));
                        double weight = Double.parseDouble(document.getString("weight"));
                        int age = Integer.parseInt(document.getString("age"));
                        Toast.makeText(ImcActivity.this, "height"+height+"weight"+weight, Toast.LENGTH_SHORT).show();

                        // Convertir altura a metros
                        double heightInM = height / 100;
                        // Calcular IMC
                        double imc = weight / Math.pow(heightInM, 2);

                        // Guardar IMC en Firestore
                        db.collection("users").document(email)
                                .update("bmi", imc)
                                .addOnSuccessListener(aVoid -> {
                                    // Mostrar resultados
                                    ageTxt.setText(String.valueOf(age));
                                    imcTxt.setText(String.format("%.2f", imc));
                                    conditionTxt.setText(Utils.getCategory((float) imc,this));
                                    suggestionTxt.setText(Utils.getSuggestions((float) imc, this));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(ImcActivity.this, "Error saving IMC", Toast.LENGTH_SHORT).show();
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

    private void redirectActivity(){
        nextButton.setOnClickListener(view -> {
            Intent intent = new Intent(ImcActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

}