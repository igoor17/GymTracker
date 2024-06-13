package es.eduardo.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Activity for handling password reset functionality.
 */
public class ForgotPassActivity extends AppCompatActivity {

    // Firebase Authentication instance
    FirebaseAuth mAuth;

    // UI elements
    EditText email;
    Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.forgot_email);
        resetButton = findViewById(R.id.resPass);

        // Initialize password reset process
        resetPassword();
    }

    /**
     * Sets up the OnClickListener for the reset password button.
     * Performs the password reset operation when the button is clicked.
     */
    private void resetPassword(){
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = email.getText().toString();

                if(emailText.isEmpty()){
                    Toast.makeText(ForgotPassActivity.this, "Please fill the email field", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email
                mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ForgotPassActivity.this, "Email sent", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ForgotPassActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
