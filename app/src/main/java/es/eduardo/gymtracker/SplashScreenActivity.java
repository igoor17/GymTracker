package es.eduardo.gymtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load language preference
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("language", "en"); // Default language is English

        // Set app locale to the saved language
        setAppLocale(languageCode);
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_DISPLAY_LENGTH = 2000; // Duration of the splash screen in milliseconds

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void setAppLocale(String languageCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(languageCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }
}