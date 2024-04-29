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

import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class NewUserInfoActivity extends AppCompatActivity {

    //Firebase
    FirebaseFirestore db;
    FirebaseAuth mAuth;

    //Weight
    RelativeLayout weightPlus;
    RelativeLayout weightMinus;
    TextView weightTxt;
    int userWeightValue=50;

    //Height
    SeekBar seekBar;
    TextView userHeight;
    int userHeightValue;

    //Age
    RelativeLayout agePlus;
    RelativeLayout ageMinus;
    TextView ageTxt;
    int userAgeValue=19;

    //Gender
    TextView maleTxt;
    TextView femaleTxt;
    String genderValue;

    //NextButton
    Button nextButton;

    //Executor
    Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_info);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

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
        nextButton= findViewById(R.id.nextButton);

        // Initialize the executor
        executor = Executors.newSingleThreadExecutor();

        seekBarProgress();
        weightUserProgress();
        ageUserProgress();
        genderSelect();
        saveUserInfo();

    }

    private void saveUserInfo() {
        nextButton.setOnClickListener(view -> {
            String userAge = String.valueOf(userAgeValue);
            String userWeight = String.valueOf(userWeightValue);
            String userHeight = String.valueOf(userHeightValue);

            // Create a new user object with the user's information
            Map<String, Object> user = new HashMap<>();
            user.put("age", userAge);
            user.put("weight", userWeight);
            user.put("height", userHeight);
            user.put("gender", genderValue);


            // Get the current user's email
            String userEmail = mAuth.getCurrentUser().getEmail();

            // Update the user's information in the database
            executor.execute(() -> {
                db.collection("users").document(userEmail)
                        .update(user)
                        .addOnSuccessListener(aVoid -> {
                            // User information updated successfully
                            // Redirect the user to the next activity
                            Intent intent = new Intent(NewUserInfoActivity.this, ImcActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            // User information failed to update
                            // Display an error message to the user
                            runOnUiThread(() -> Toast.makeText(NewUserInfoActivity.this, "Failed to update user information", Toast.LENGTH_SHORT).show());
                        });
            });
        });
    }


    private void seekBarProgress() {

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                userHeight.setText(progress + " cm");
                userHeightValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void weightUserProgress(){

        final Handler handler = new Handler();
        final int delay = 100; //milliseconds

        weightPlus.setOnClickListener(v -> {
            int weight = Integer.parseInt(weightTxt.getText().toString());
            weight++;
            weightTxt.setText(String.valueOf(weight));
            userWeightValue = weight;
        });

        weightPlus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable(){
                public void run(){
                    int weight = Integer.parseInt(weightTxt.getText().toString());
                    weight++;
                    weightTxt.setText(String.valueOf(weight));
                    userWeightValue = weight;
                    handler.postDelayed(this, delay);
                }
            }, delay);
            return true;
        });

        weightPlus.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacksAndMessages(null);
            }
            return false;
        });


        weightMinus.setOnClickListener(v -> {
            int weight = Integer.parseInt(weightTxt.getText().toString());
            weight--;
            weightTxt.setText(String.valueOf(weight));
            userWeightValue = weight;
        });

        weightMinus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable(){
                public void run(){
                    int weight = Integer.parseInt(weightTxt.getText().toString());
                    weight--;
                    weightTxt.setText(String.valueOf(weight));
                    userWeightValue = weight;
                    handler.postDelayed(this, delay);
                }
            }, delay);
            return true;
        });

        weightMinus.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacksAndMessages(null);

            }
            return false;
        });

    }

    private void ageUserProgress(){

        final Handler handler = new Handler();
        final int delay = 100; //milliseconds

        agePlus.setOnClickListener(v -> {
            int age = Integer.parseInt(ageTxt.getText().toString());
            age++;
            ageTxt.setText(String.valueOf(age));
            userAgeValue = age;
        });

        agePlus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable(){
                public void run(){
                    int age = Integer.parseInt(ageTxt.getText().toString());
                    age++;
                    ageTxt.setText(String.valueOf(age));
                    userAgeValue = age;
                    handler.postDelayed(this, delay);
                }
            }, delay);
            return true;
        });

        agePlus.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacksAndMessages(null);
            }
            return false;
        });

        ageMinus.setOnClickListener(v -> {
            int age = Integer.parseInt(ageTxt.getText().toString());
            age--;
            ageTxt.setText(String.valueOf(age));
            userAgeValue = age;
        });

        ageMinus.setOnLongClickListener(v -> {
            handler.postDelayed(new Runnable(){
                public void run(){
                    int age = Integer.parseInt(ageTxt.getText().toString());
                    age--;
                    ageTxt.setText(String.valueOf(age));
                    userAgeValue = age;
                    handler.postDelayed(this, delay);
                }
            }, delay);
            return true;
        });

        ageMinus.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP){
                handler.removeCallbacksAndMessages(null);
            }
            return false;
        });

    }

    private void genderSelect(){
        maleTxt.setOnClickListener(view -> {
            // Change the appearance of the TextViews to indicate selection
            maleTxt.setBackgroundColor(Color.LTGRAY); // Change color to indicate selection
            femaleTxt.setBackgroundColor(Color.TRANSPARENT); // Remove selection

            // Update genderValue
            genderValue = "male";
        });

        femaleTxt.setOnClickListener(view -> {
            // Change the appearance of the TextViews to indicate selection
            femaleTxt.setBackgroundColor(Color.LTGRAY); // Change color to indicate selection
            maleTxt.setBackgroundColor(Color.TRANSPARENT); // Remove selection

            // Update genderValue
            genderValue = "female";
        });
    }
}