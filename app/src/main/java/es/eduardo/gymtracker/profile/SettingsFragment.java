package es.eduardo.gymtracker.profile;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.eduardo.gymtracker.LoginActivity;
import es.eduardo.gymtracker.MainActivity;
import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.AlarmReceiver;
import es.eduardo.gymtracker.utils.LanguageAdapter;
import es.eduardo.gymtracker.utils.LanguageItem;

/**
 * Fragment for managing user settings including language selection, notifications, and logout functionality.
 */
public class SettingsFragment extends Fragment {

    // UI elements
    Spinner languageSpinner;
    Button saveButton;
    Button logoutButton;
    TextView helpText;
    Switch weightSwitch;

    // Selected language code
    String selectedLanguageCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        languageSpinner = view.findViewById(R.id.language_spinner);
        saveButton = view.findViewById(R.id.save_language_button);
        logoutButton = view.findViewById(R.id.logout_button);
        helpText = view.findViewById(R.id.help_text_view);
        weightSwitch = view.findViewById(R.id.switch_notifications);

        // Underline the help text
        String text = helpText.getText().toString();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), 0);
        helpText.setText(spannableString);

        // Load notification preference
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("NotificationPref", Context.MODE_PRIVATE);
        boolean isNotificationOn = sharedPreferences.getBoolean("isNotificationOn", false); // Default is false
        weightSwitch.setChecked(isNotificationOn);

        // Initialize language spinner
        List<LanguageItem> languageItems = new ArrayList<>();
        languageItems.add(new LanguageItem(getString(R.string.english), R.drawable.en_flag));
        languageItems.add(new LanguageItem(getString(R.string.spanish), R.drawable.sp_flag));
        LanguageAdapter languageAdapter = new LanguageAdapter(getContext(), languageItems);
        languageSpinner.setAdapter(languageAdapter);

        // Load saved language preference
        String savedLanguage = loadLanguagePreference();
        int languagePosition = getLanguagePosition(savedLanguage);
        languageSpinner.setSelection(languagePosition);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LanguageItem selectedLanguage = (LanguageItem) parent.getItemAtPosition(position);
                selectedLanguageCode = selectedLanguage.getLanguage().equals(getString(R.string.english)) ? "en" : "es";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            if (weightSwitch.isChecked()) {
                setWeightReminder();
            } else {
                cancelWeightReminder();
            }
            saveNotificationPreference(weightSwitch.isChecked());
            saveLanguage();
        });

        // Logout button click listener
        logoutButton.setOnClickListener(v -> {
            logout();
        });

        // Help text click listener
        helpText.setOnClickListener(v -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new HelpFragment());
            transaction.commit();
        });

        return view;
    }

    /**
     * Saves the selected language code to preferences, changes the app locale, and restarts MainActivity.
     */
    private void saveLanguage() {
        setAppLocale(selectedLanguageCode);
        saveLanguagePreference(selectedLanguageCode);

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Logs out the current user and navigates to LoginActivity.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Saves the selected language preference to SharedPreferences.
     *
     * @param language The selected language code ("en" for English, "es" for Spanish).
     */
    private void saveLanguagePreference(String language) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }

    /**
     * Loads the saved language preference from SharedPreferences.
     *
     * @return The saved language code, defaulting to "en" (English) if not found.
     */
    private String loadLanguagePreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("language", "en"); // Default language is English
    }

    /**
     * Sets the app's locale based on the selected language code.
     *
     * @param languageCode The language code to set ("en" for English, "es" for Spanish).
     */
    private void setAppLocale(String languageCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(languageCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    /**
     * Retrieves the position of the given language code in the language spinner.
     *
     * @param languageCode The language code to find position for ("en" or "es").
     * @return The position of the language in the spinner (0 for English, 1 for Spanish).
     */
    private int getLanguagePosition(String languageCode) {
        String english = getString(R.string.english);
        String spanish = getString(R.string.spanish);
        if (languageCode.equals("en")) {
            return english.equals(((LanguageItem) languageSpinner.getItemAtPosition(0)).getLanguage()) ? 0 : 1;
        } else if (languageCode.equals("es")) {
            return spanish.equals(((LanguageItem) languageSpinner.getItemAtPosition(0)).getLanguage()) ? 0 : 1;
        }
        return 0; // Default to English
    }

    /**
     * Sets a weekly alarm reminder for weight tracking, if permission is granted.
     */
    private void setWeightReminder() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SET_ALARM) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SET_ALARM}, 1);
        } else {
            // Permission has already been granted, set the alarm
            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm to start at 9:00 a.m. on Sunday, repeating weekly
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.set(Calendar.HOUR_OF_DAY, 9);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }

    /**
     * Saves the notification preference (on/off) to SharedPreferences.
     *
     * @param isOn Boolean indicating whether notifications are turned on or off.
     */
    private void saveNotificationPreference(boolean isOn) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("NotificationPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isNotificationOn", isOn);
        editor.apply();
    }

    /**
     * Cancels the weekly weight reminder alarm.
     */
    private void cancelWeightReminder() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}
