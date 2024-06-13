package es.eduardo.gymtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.eduardo.gymtracker.databinding.ActivityMainBinding;
import es.eduardo.gymtracker.map.GymsFragment;
import es.eduardo.gymtracker.profile.ProfileFragment;
import es.eduardo.gymtracker.routines.NewRoutinesFragment;
import es.eduardo.gymtracker.store.StoreFragment;
import es.eduardo.gymtracker.utils.IMCWorker;

/**
 * MainActivity is the main entry point of the GymTracker application after user authentication.
 * It manages navigation, permissions, and background tasks scheduling.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1; // Request code for location permission
    ActivityMainBinding binding;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, redirect to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Remove background from BottomNavigationView for better UI integration
        binding.bottomNavigationView.setBackground(null);

        // Start with HomeFragment as the default fragment
        replaceFragment(new HomeFragment());

        // Request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        // Handle navigation item clicks in BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.gyms) {
                replaceFragment(new GymsFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.store) {
                replaceFragment(new StoreFragment());
            }
            return false;
        });

        // Handle click on addRoutines button to navigate to NewRoutinesFragment
        binding.addRoutines.setOnClickListener(v -> {
            replaceFragment(new NewRoutinesFragment());
        });

        // Schedule a weekly background task using WorkManager
        scheduleWeeklyTask();
    }

    /**
     * Replaces the current fragment in MainActivity's frame layout with a new fragment.
     * @param fragment The fragment to replace the current fragment with.
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Handles the result of location permission request.
     * @param requestCode The request code passed to requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The results for each permission requested.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, getString(R.string.location_granted), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Schedules a weekly background task using WorkManager.
     * The task is scheduled to run every Monday at 9:00 AM.
     */
    private void scheduleWeeklyTask() {
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long nextMonday = calendar.getTimeInMillis();

        if (currentTime > nextMonday) {
            calendar.add(Calendar.DAY_OF_YEAR, 7);
            nextMonday = calendar.getTimeInMillis();
        }

        long delay = nextMonday - currentTime;
        long delayInMinutes = TimeUnit.MILLISECONDS.toMinutes(delay);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(IMCWorker.class, 7, TimeUnit.DAYS)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
