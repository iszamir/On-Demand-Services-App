package com.example.customtoolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FrameLayout frameLayout = findViewById(R.id.frame_layout);

        // Set Home fragment as default
        replaceFragment(new HomeFragment());

        // Find views using findViewById
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        // BottomNavigationView item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        //Receive uploadedImageUri from UploadProfilePictureActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("uploadedImageUri")) {
            Uri uploadedImageUri = intent.getParcelableExtra("uploadedImageUri");
            //Send URI to ProfileFragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("imageUri", uploadedImageUri);
            ProfileFragment fragment = new ProfileFragment();
            fragment.setArguments(bundle);

            //Replace the existing fragment with ProfileFragment
            replaceFragment(fragment);
        }



        //Check for the flag to open ProfileFragment
        if (getIntent().getBooleanExtra("open_profile_fragment", false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = ProfileFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }

        // Inside onCreate() or wherever you create your ProfileFragment instance
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setOnBackPressedListener(() -> {
            // Handle back button press from ProfileFragment
            replaceFragment(new HomeFragment());
        });
        if (getIntent().getBooleanExtra("open_profile_fragment", false)) {
            replaceFragment(profileFragment);
        }



        //Check if the intent has extra for navigating to homeFragment
        if (getIntent().getBooleanExtra("open_home_fragment", false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = HomeFragment.newInstance();
            fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }

        // Inside onCreate() or wherever you create your ProfileFragment instance
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnBackPressedListener(() -> {
            // Handle back button press from ProfileFragment
            replaceFragment(new HomeFragment());
        });
        if (getIntent().getBooleanExtra("open_home_fragment", false)) {
            replaceFragment(homeFragment);
        }

    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            //Refresh action
            startActivity(getIntent());
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(MainActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(MainActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(MainActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        }  else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, Login.class);

            //Clear stack to prevent user coming back to Home Fragment/MainActivity after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true; // Return true to indicate that the back button press is handled
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        // Handle back button press based on the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof ProfileFragment) {
            // Delegate back button press to ProfileFragment's listener
            ((ProfileFragment) currentFragment).handleBackPressed();
        } else {
            // Default back button behavior for other fragments
            super.onBackPressed();
        }
    }
}