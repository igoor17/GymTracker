package es.eduardo.gymtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class NewUserInfoActivity extends AppCompatActivity {

    // Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    // Weight
    RelativeLayout weightPlus;
    RelativeLayout weightMinus;
    TextView weightTxt;
    int userWeightValue = 50;

    // Height
    SeekBar seekBar;
    TextView userHeight;
    int userHeightValue;

    // Age
    RelativeLayout agePlus;
    RelativeLayout ageMinus;
    TextView ageTxt;
    int userAgeValue = 19;

    // Gender
    TextView maleTxt;
    TextView femaleTxt;
    String genderValue;

    // NextButton
    Button nextButton;

    // Executor for database operations
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        seekBar = findViewById(R.id.Seekbar);
        userHeight = findViewById(R.id.height_txt);
        weightPlus = findViewById(R.id.weight_plus);
        weightMinus = findViewById(R.id.weight_minus);
        weightTxt = findViewById(R.id.weight_txt);
        agePlus = findViewById(R.id.age_plus);
        ageMinus = findViewById(R.id.age_minus);
        ageTxt = findViewById(R.id.age_txt);
        maleTxt = findViewById(R.id.maleTxt);
        femaleTxt = findViewById(R.id.femaleTxt);
        nextButton = findViewById(R.id.nextButton);

        // Initialize the executor for background tasks
        executor = Executors.newSingleThreadExecutor();

        // Setup UI components and listeners
        seekBarProgress();
        weightUserProgress();
        ageUserProgress();
        genderSelect();
        saveUserInfo();
    }

    // Method to save user information to Firebase
    private void saveUserInfo() {
        nextButton.setOnClickListener(view -> {
            // Retrieve user input values
            String userAge = String.valueOf(userAgeValue);
            String userWeight = String.valueOf(userWeightValue);
            String userHeight = String.valueOf(userHeightValue);

            // Create a map to store user data
            Map<String, Object> user = new HashMap<>();
            user.put("age", userAge);
            user.put("weight", userWeight);
            user.put("height", userHeight);
            user.put("gender", genderValue);

            // Get the current user's email from Firebase authentication
            String userEmail = mAuth.getCurrentUser().getEmail();

            // Update user data in Firestore database
            executor.execute(() -> {
                db.collection("users").document(userEmail)
                        .update(user)
                        .addOnSuccessListener(aVoid -> {
                            // On successful update, proceed to next activity (ImcActivity)
                            Intent intent = new Intent(NewUserInfoActivity.this, ImcActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // Display error message if update fails
                            runOnUiThread(() -> Toast.makeText(NewUserInfoActivity.this, getString(R.string.failed_update_user), Toast.LENGTH_SHORT).show());
                        });
            });
        });
    }

    // Method to handle SeekBar progress changes for height
    private void seekBarProgress() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update TextView with selected height in cm
                userHeight.setText(progress + " cm");
                userHeightValue = progress; // Update userHeightValue with selected height
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // Method to handle weight adjustments
    private void weightUserProgress() {
        final Handler handler = new Handler();
        final int delay = 100; // Delay for long press actions

        // OnClickListener for weight increase
        weightPlus.setOnClickListener(v -> {
            int weight = Integer.parseInt(weightTxt.getText().toString());
            weight++;
            weightTxt.setText(String.valueOf(weight));
            userWeightValue = weight; // Update userWeightValue
        });

        // LongClickListener for continuous weight increase
        weightPlus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable() {
                public void run() {
                    int weight = Integer.parseInt(weightTxt.getText().toString());
                    weight++;
                    weightTxt.setText(String.valueOf(weight));
                    userWeightValue = weight; // Update userWeightValue
                    handler.postDelayed(this, delay); // Repeat the action with delay
                }
            }, delay);
            return true;
        });

        // TouchListener to stop continuous weight increase on touch release
        weightPlus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacksAndMessages(null); // Remove callbacks to stop repeating action
            }
            return false;
        });

        // Similar setup for weight decrease
        weightMinus.setOnClickListener(v -> {
            int weight = Integer.parseInt(weightTxt.getText().toString());
            weight--;
            weightTxt.setText(String.valueOf(weight));
            userWeightValue = weight; // Update userWeightValue
        });

        weightMinus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable() {
                public void run() {
                    int weight = Integer.parseInt(weightTxt.getText().toString());
                    weight--;
                    weightTxt.setText(String.valueOf(weight));
                    userWeightValue = weight; // Update userWeightValue
                    handler.postDelayed(this, delay); // Repeat the action with delay
                }
            }, delay);
            return true;
        });

        weightMinus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacksAndMessages(null); // Remove callbacks to stop repeating action
            }
            return false;
        });
    }

    // Method to handle age adjustments
    private void ageUserProgress() {
        final Handler handler = new Handler();
        final int delay = 100; // Delay for long press actions

        // OnClickListener for age increase
        agePlus.setOnClickListener(v -> {
            int age = Integer.parseInt(ageTxt.getText().toString());
            age++;
            ageTxt.setText(String.valueOf(age));
            userAgeValue = age; // Update userAgeValue
        });

        // LongClickListener for continuous age increase
        agePlus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable() {
                public void run() {
                    int age = Integer.parseInt(ageTxt.getText().toString());
                    age++;
                    ageTxt.setText(String.valueOf(age));
                    userAgeValue = age; // Update userAgeValue
                    handler.postDelayed(this, delay); // Repeat the action with delay
                }
            }, delay);
            return true;
        });

        // TouchListener to stop continuous age increase on touch release
        agePlus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacksAndMessages(null); // Remove callbacks to stop repeating action
            }
            return false;
        });

        // Similar setup for age decrease
        ageMinus.setOnClickListener(v -> {
            int age = Integer.parseInt(ageTxt.getText().toString());
            age--;
            ageTxt.setText(String.valueOf(age));
            userAgeValue = age; // Update userAgeValue
        });

        ageMinus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable() {
                public void run() {
                    int age = Integer.parseInt(ageTxt.getText().toString());
                    age--;
                    ageTxt.setText(String.valueOf(age));
                    userAgeValue = age; // Update userAgeValue
                    handler.postDelayed(this, delay); // Repeat the action with delay
                }
            }, delay);
            return true;
        });

        ageMinus.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handler.removeCallbacksAndMessages(null); // Remove callbacks to stop repeating action
            }
            return false;
        });
    }

    // Method to handle gender selection
    private void genderSelect() {
        maleTxt.setOnClickListener(view -> {
            // Change the appearance of TextViews to indicate selection
            maleTxt.setBackgroundColor(Color.LTGRAY); // Change color to indicate selection
            femaleTxt.setBackgroundColor(Color.TRANSPARENT); // Remove selection from other TextView

            // Update genderValue
            genderValue = "male";
        });

        femaleTxt.setOnClickListener(view -> {
            // Change the appearance of TextViews to indicate selection
            femaleTxt.setBackgroundColor(Color.LTGRAY); // Change color to indicate selection
            maleTxt.setBackgroundColor(Color.TRANSPARENT); // Remove selection from other TextView

            // Update genderValue
            genderValue = "female";
        });
    }
}
