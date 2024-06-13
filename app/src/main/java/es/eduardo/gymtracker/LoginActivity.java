package es.eduardo.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Activity responsible for user login using email/password or Google Sign-In.
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    // UI
    EditText username, password;
    Button loginButton;
    TextView RedirectSignUpText;
    TextView RedirectForgotPassText;
    Button googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.loginButton);
        RedirectSignUpText = findViewById(R.id.redirectSignupText);
        RedirectForgotPassText = findViewById(R.id.forgotPasswordText);
        googleLoginButton = findViewById(R.id.googleLoginButton);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listener for Google Sign-In button
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        // Set click listeners for redirecting to Signup and Forgot Password activities
        redirectSignUp();
        redirectForgotPass();

        // Set click listener for the login button
        login();
    }

    /**
     * Redirects the user to the Signup activity when clicked.
     */
    private void redirectSignUp() {
        RedirectSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Redirects the user to the Forgot Password activity when clicked.
     */
    private void redirectForgotPass() {
        RedirectForgotPassText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Handles user login based on username/email and password.
     */
    private void login() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameOrEmail = username.getText().toString();
                String passwordText = password.getText().toString();

                if (usernameOrEmail.isEmpty() || passwordText.isEmpty()) {
                    return;
                }

                // Attempt to find the username in Firestore
                db.collection("users")
                        .whereEqualTo("username", usernameOrEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // Username exists, get the associated email
                                        String email = task.getResult().getDocuments().get(0).getString("email");

                                        // Sign in with FirebaseAuth
                                        mAuth.signInWithEmailAndPassword(email, passwordText)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Successful login
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            // If login fails, display a message to the user
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        // Username doesn't exist, assume input is an email and try to login
                                        mAuth.signInWithEmailAndPassword(usernameOrEmail, passwordText)
                                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if (task.isSuccessful()) {
                                                            // Successful login
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            // If login fails, display a message to the user
                                                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
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

    /**
     * Handles Google Sign-In authentication.
     * @param idToken The ID token received from Google Sign-In.
     */
    private void loginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Initiates Google Sign-In by starting the intent from GoogleSignInClient.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Redirects the user to the appropriate activity based on their sign-in status.
     * @param user The FirebaseUser object representing the signed-in user.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getMetadata().getCreationTimestamp() == user.getMetadata().getLastSignInTimestamp()) {
                // New user, redirect to NewUserInfoActivity
                Intent intent = new Intent(LoginActivity.this, NewUserInfoActivity.class);
                startActivity(intent);
            } else {
                // Existing user, redirect to MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
