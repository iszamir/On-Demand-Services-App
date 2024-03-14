package com.example.customtoolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Update Email");
        //Enable Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        editTextPassword = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        buttonUpdateEmail = findViewById(R.id.btn_update_email);

        // Make Update Email button disable in the beginning until the user is verified
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Set old email ID on TextView
        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong! User's details not available", Toast.LENGTH_SHORT).show();
            // Handle the situation where the user is not available, maybe redirect to login or handle accordingly
        } else {
            userOldEmail = firebaseUser.getEmail();
            TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
            textViewOldEmail.setText(userOldEmail);

            reAuthenticate();
        }
    }


    //ReAuthenticate/Verify user before updating email
    private void reAuthenticate() {
        Button buttonVerifyUser = findViewById(R.id.btn_authenticate_user);
        buttonVerifyUser.setOnClickListener(v -> {

            //Obtain password for authentication
            userPassword = editTextPassword.getText().toString();

            if (TextUtils.isEmpty(userPassword)) {
                Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Please enter your password for authentication");
                editTextPassword.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPassword);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UpdateEmailActivity.this, "Password has been verified."
                                + "You can update your email now", Toast.LENGTH_LONG).show();

                        //Set TextView to show user is authenticated
                        textViewAuthenticated.setText("You are authenticated. You can update your email now");

                        //Disable EditText for password, button to verify. Enable EditText for new Email and Update Email button
                        editTextNewEmail.setEnabled(true);
                        buttonUpdateEmail.setEnabled(true);
                        editTextPassword.setEnabled(false);
                        buttonVerifyUser.setEnabled(false);

                        //Change color of Update Email button
                        buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList
                                (UpdateEmailActivity.this, R.color.dark_green));


                        buttonUpdateEmail.setOnClickListener(v1 -> {
                            userNewEmail = editTextNewEmail.getText().toString();
                            if (TextUtils.isEmpty(userNewEmail)) {
                                Toast.makeText(UpdateEmailActivity.this, "New email is required", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Please enter a new Email");
                                editTextNewEmail.requestFocus();
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                Toast.makeText(UpdateEmailActivity.this, "PLease enter a valid Email", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Please provide a valid Email");
                                editTextNewEmail.requestFocus();
                            } else if (userOldEmail.matches(userNewEmail)) {
                                Toast.makeText(UpdateEmailActivity.this, "New Email cannot be the same as the Old Email", Toast.LENGTH_SHORT).show();
                                editTextNewEmail.setError("Please enter a new Email");
                                editTextNewEmail.requestFocus();
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                updateEmail(firebaseUser);
                            }
                        });

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(updateTask -> {
            if (updateTask.isComplete()) {

                // Verify by sending them Email
                firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        Toast.makeText(UpdateEmailActivity.this, "Email has been updated. Please verify your new Email", Toast.LENGTH_LONG).show();

                        // Stop user from returning to UpdateEmailActivity on pressing back button and close activity
                        Intent intent = NavigationUtils.openProfileFragment(UpdateEmailActivity.this);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });
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
            //Refresh activity
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateEmailActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(UpdateEmailActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateEmailActivity.this, Login.class);

            //Clear stack to prevent user coming back to Home Fragment/MainActivity after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = NavigationUtils.openProfileFragment(UpdateEmailActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}