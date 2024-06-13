package es.eduardo.gymtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * RegisterActivity handles user registration using Firebase Authentication and Firestore.
 */
public class RegisterActivity extends AppCompatActivity {

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    // UI
    EditText name, username, email, password, confirmPassword;
    Button signUpButton;

    // Executor for background tasks
    Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        name = findViewById(R.id.signup_name);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        signUpButton = findViewById(R.id.signUpButton);

        // Register button click listener
        register();
    }

    /**
     * Handles the registration process when the signUpButton is clicked.
     */
    private void register() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String nameText = name.getText().toString();
                        String usernameText = username.getText().toString();
                        String emailText = email.getText().toString();
                        String passwordText = password.getText().toString();
                        String confirmPasswordText = confirmPassword.getText().toString();

                        // Validate input fields
                        if (nameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        if (passwordText.length() < 6) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.pass_short), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        if (!passwordText.equals(confirmPasswordText)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.pass_dont_match), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        // Check if username already exists in Firestore
                        db.collection("users")
                                .whereEqualTo("username", usernameText)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().isEmpty()) {
                                                // Username does not exist, proceed with account creation
                                                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    // Registration successful, save user data to Firestore
                                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                                    Map<String, Object> userMap = new HashMap<>();
                                                                    userMap.put("name", nameText);
                                                                    userMap.put("username", usernameText);
                                                                    userMap.put("email", emailText);

                                                                    db.collection("users").document(user.getEmail())
                                                                            .set(userMap)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // Document was successfully written
                                                                                    Intent intent = new Intent(RegisterActivity.this, NewUserInfoActivity.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Write failed
                                                                                    Toast.makeText(RegisterActivity.this, getString(R.string.failed_to_write_document), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                } else {
                                                                    // Registration failed
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(RegisterActivity.this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Username already exists
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(RegisterActivity.this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } else {
                                            // Error getting documents
                                            Log.d("Firestore Error", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                });
            }
        });
    }
}
