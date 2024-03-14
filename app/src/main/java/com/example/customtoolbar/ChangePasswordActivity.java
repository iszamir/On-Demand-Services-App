package com.example.customtoolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editTextPwdCurrent, editTextPwdNew, editTextPwdConfirm;
    private TextView textViewAuthenticated;
    private Button buttonChangedPwd, buttonReauthenticate;
    private ProgressBar progressBar;
    private String userCurrentPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Password");
        //Enable Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         editTextPwdCurrent = findViewById(R.id.editText_change_pwd_current);
         editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
         editTextPwdConfirm = findViewById(R.id.editText_change_pwd_new_confirm);
         textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
         progressBar = findViewById(R.id.progressBar);
         buttonChangedPwd = findViewById(R.id.button_change_pwd);
         buttonReauthenticate = findViewById(R.id.button_change_pwd_authenticate);

         //Disable edittext for New Password, Confirm Password and Make Change Pwd Button unclickable until user is authenticated
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirm.setEnabled(false);
        buttonChangedPwd.setEnabled(false);

        //Get current user using mAuth and save into firebaseUser variable
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong, user's details is not available.", Toast.LENGTH_SHORT).show();

            //Open ProfileFragment Activity and Stop user from returning to ChangePassword Activity on pressing back button and close activity
            Intent intent = NavigationUtils.openProfileFragment(ChangePasswordActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    //ReAuthenticate before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReauthenticate.setOnClickListener(v -> {
            //Save current pwd in a string
            userCurrentPwd = editTextPwdCurrent.getText().toString();

            //Check if user has entered a password or not
            if (TextUtils.isEmpty(userCurrentPwd)) {
                Toast.makeText(ChangePasswordActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                editTextPwdCurrent.setError("Please enter your password to authenticate");
                editTextPwdCurrent.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                //ReAuthenticate user now
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userCurrentPwd);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        //Disable editText for current password and authenticate btn and enable editText for new pwd,
                        // confirm pwd and change pwd button
                        editTextPwdCurrent.setEnabled(false);
                        buttonReauthenticate.setEnabled(false);
                        editTextPwdNew.setEnabled(true);
                        editTextPwdConfirm.setEnabled(true);
                        buttonChangedPwd.setEnabled(true);

                        //Set TextView to show user is authenticated
                        textViewAuthenticated.setText(R.string.authenticated);

                        //Change Password change button color
                        buttonChangedPwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                ChangePasswordActivity.this, R.color.dark_green));

                        buttonChangedPwd.setOnClickListener(v1 -> changePassword(firebaseUser));
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });


    }

    private void changePassword(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirm = editTextPwdConfirm.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(this, "New Password is needed", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new Password");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirm)) {
            Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
            editTextPwdConfirm.setError("Please re-enter your new Password");
            editTextPwdConfirm.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirm)) {
            Toast.makeText(this, "Password did not match", Toast.LENGTH_SHORT).show();
            editTextPwdConfirm.setError("Please re-enter the same Password");
            editTextPwdConfirm.requestFocus();
        } else if (userCurrentPwd.matches(userPwdNew)) {
            Toast.makeText(this, "New Password cannot be same as old Password", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new Password");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password has been changed", Toast.LENGTH_SHORT).show();

                    // Open ProfileFragment Activity and Stop user from returning to ChangePasswordActivity on pressing back button and close activity
                    Intent intent = NavigationUtils.openProfileFragment(ChangePasswordActivity.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }


    }

    //Creating Action Bar Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //When any Menu Item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh) {
            //Refresh activity
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, Login.class);

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
        Intent intent = NavigationUtils.openProfileFragment(ChangePasswordActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}