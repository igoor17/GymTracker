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

        // Load language preference from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        String languageCode = sharedPreferences.getString("language", "en"); // Default language is English

        // Set the app locale to the saved language
        setAppLocale(languageCode);

        setContentView(R.layout.activity_splash_screen);

        int SPLASH_DISPLAY_LENGTH = 2000; // Duration of the splash screen in milliseconds

        // Handler to delay the intent to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // Finish the current activity
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    /**
     * Sets the application locale based on the language code.
     *
     * @param languageCode The language code (e.g., "en" for English, "es" for Spanish).
     */
    private void setAppLocale(String languageCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        // Create a Locale using the language code
        Locale locale = new Locale(languageCode.toLowerCase());

        // Set the locale in the configuration
        conf.setLocale(locale);

        // Update the resources and configuration with the new locale
        res.updateConfiguration(conf, dm);
    }
}
