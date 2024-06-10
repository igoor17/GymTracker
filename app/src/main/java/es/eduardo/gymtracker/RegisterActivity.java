package es.eduardo.gymtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    //UI
    EditText name, username, email, password, confirmPassword;
    Button signUpButton;

    Executor executor = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        name = findViewById(R.id.signup_name);
        username = findViewById(R.id.signup_username);
        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        confirmPassword = findViewById(R.id.signup_confirm_password);
        signUpButton = findViewById(R.id.signUpButton);

        register();

    }

    private void register(){
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

                        if(nameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        if(passwordText.length() < 6) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.pass_short), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

                        if(!passwordText.equals(confirmPasswordText)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, getString(R.string.pass_dont_match), Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }

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
                                                                    // Code to execute after successful registration
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
                                                                    // If sign in fails, display a message to the user.
                                                                    runOnUiThread(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            Toast.makeText(RegisterActivity.this, getString(R.string.auth_failed),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            } else {
                                                // Nombre de usuario existe
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(RegisterActivity.this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } else {
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