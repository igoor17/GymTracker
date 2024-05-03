package es.eduardo.gymtracker.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.eduardo.gymtracker.LoginActivity;
import es.eduardo.gymtracker.MainActivity;
import es.eduardo.gymtracker.R;
import es.eduardo.gymtracker.utils.LanguageAdapter;
import es.eduardo.gymtracker.utils.LanguageItem;

public class SettingsFragment extends Fragment {

    // UI
    Spinner languageSpinner;
    Button saveButton;
    Button logoutButton;

    // Idioma seleccionado
    String selectedLanguageCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        languageSpinner = view.findViewById(R.id.language_spinner);
        saveButton = view.findViewById(R.id.save_language_button);
        logoutButton = view.findViewById(R.id.logout_button);

        List<LanguageItem> languageItems = new ArrayList<>();
        languageItems.add(new LanguageItem(getString(R.string.english), R.drawable.en_flag));
        languageItems.add(new LanguageItem(getString(R.string.spanish), R.drawable.sp_flag));

        LanguageAdapter languageAdapter = new LanguageAdapter(getContext(), languageItems);

        languageSpinner.setAdapter(languageAdapter);

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
            }
        });

        saveButton.setOnClickListener(v -> {
            saveLanguage();
        });

        logoutButton.setOnClickListener(v -> {
            logout();
        });

        return view;
    }

    private void saveLanguage() {
        setAppLocale(selectedLanguageCode);
        saveLanguagePreference(selectedLanguageCode);

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveLanguagePreference(String language) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("language", language);
        editor.apply();
    }

    private String loadLanguagePreference() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LanguagePref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("language", "en"); // Default language is English
    }

    private void setAppLocale(String languageCode) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(languageCode.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

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
}