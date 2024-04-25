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

public class RegisterActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseStorage storage;
    //UI
    EditText name, username, email, password, confirmPassword;
    Button signUpButton;
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
                String nameText = name.getText().toString();
                String usernameText = username.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                String confirmPasswordText = confirmPassword.getText().toString();

                if(nameText.isEmpty() || usernameText.isEmpty() || emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!passwordText.equals(confirmPasswordText)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
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
                                                            FirebaseUser user = mAuth.getCurrentUser();

                                                            // Create a new user with name and username
                                                            Map<String, Object> userMap = new HashMap<>();
                                                            userMap.put("name", nameText);
                                                            userMap.put("username", usernameText);
                                                            userMap.put("email", emailText);
                                                            // Add a new document with the user's ID
                                                            db.collection("users").document(user.getEmail())
                                                                    .set(userMap)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            // Convert the drawable to a bitmap
                                                                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);

                                                                            // Convert the bitmap to a byte array
                                                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                                            byte[] data = baos.toByteArray();

                                                                            // Create a reference to 'images/userID.jpg'
                                                                            StorageReference imageRef = storage.getReference().child("images/" + user.getEmail() + ".jpg");

                                                                            // Upload the byte array to Firebase Storage
                                                                            UploadTask uploadTask = imageRef.putBytes(data);
                                                                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception exception) {
                                                                                    // Handle unsuccessful uploads
                                                                                }
                                                                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    // Get the download URL and update Firestore
                                                                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                        @Override
                                                                                        public void onSuccess(Uri uri) {
                                                                                            db.collection("users").document(user.getEmail())
                                                                                                    .update("imageUrl", uri.toString());
                                                                                        }
                                                                                    });
                                                                                }
                                                                            });
                                                                            Toast.makeText(RegisterActivity.this, "Account created.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            // Switch to the NewUserInfoActivity

                                                                            Intent intent = new Intent(RegisterActivity.this, NewUserInfoActivity.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(RegisterActivity.this, "Error adding document.",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            Log.e("Firestore Error", e.getMessage(), e);
                                                                        }
                                                                    });
                                                        } else {
                                                            // If sign in fails, display a message to the user.
                                                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Username exists, show an error message
                                        Toast.makeText(RegisterActivity.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d("Firestore Error", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

    }
}