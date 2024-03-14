package com.example.customtoolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonAuthenticate, buttonDeleteUser;
    private final static String TAG = "DeleteProfileActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Delete Profile");
        //Enable Back Button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        editTextUserPwd = findViewById(R.id.editText_delete_user);
        textViewAuthenticated = findViewById(R.id.textView_delete_user_authenticated);
        buttonAuthenticate = findViewById(R.id.button_delete_user_authenticate);
        buttonDeleteUser = findViewById(R.id.button_delete_user_pwd);

        //Disable Delete User Button until user is authenticated
        buttonDeleteUser.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(this, "Something went wrong! User details are not available " +
                    "at the moment", Toast.LENGTH_SHORT).show();
            //Open profileFragment and Stop user from returning to DeleteProfile activity on pressing back button and close activity
            Intent intent = NavigationUtils.openProfileFragment(DeleteProfileActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }


    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonAuthenticate.setOnClickListener(v -> {
            //Save current pwd in a string
            userPwd = editTextUserPwd.getText().toString();

            //Check if user has entered a password or not
            if (TextUtils.isEmpty(userPwd)) {
                Toast.makeText(DeleteProfileActivity.this, "Password is needed", Toast.LENGTH_SHORT).show();
                editTextUserPwd.setError("Please enter your password to authenticate");
                editTextUserPwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                //ReAuthenticate user now
                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userPwd);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);

                        //Disable editText for password.
                        editTextUserPwd.setEnabled(false);

                        //Enable change pwd button, disable authentication button
                        buttonAuthenticate.setEnabled(false);
                        buttonDeleteUser.setEnabled(true);

                        //Set TextView to show user is authenticated
                        textViewAuthenticated.setText(R.string.you_are_authenticated_verified_you_can_delete_your_profile_and_related_data_now);
                        Toast.makeText(DeleteProfileActivity.this, "Password has been verified", Toast.LENGTH_SHORT).show();

                        //Change Password change button color
                        buttonDeleteUser.setBackgroundTintList(ContextCompat.getColorStateList(
                                DeleteProfileActivity.this, R.color.dark_green));

                        buttonDeleteUser.setOnClickListener(v1 -> showAlertDialog());
                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                });
            }
        });


    }

    private void showAlertDialog() {
        //Setup Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User and Related Data?");
        builder.setMessage("Do you really want to delete your profile and related data? This action is irreversible!");

        //Open email apps if user clicks Continue button
        builder.setPositiveButton("Continue", (dialog, which) -> deleteUserData(firebaseUser));

        //Return to User's Profile fragment if User presses the cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            //Open profileFragment and Stop user from returning to DeleteProfile activity on pressing back button and close activity
            Intent intent = NavigationUtils.openProfileFragment(DeleteProfileActivity.this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        //Create the Alert Dialogue
        AlertDialog alertDialog = builder.create();

        //Change the button colour of Continue to red
        alertDialog.setOnShowListener(dialog -> alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red)));

        alertDialog.show();
    }

    private void deleteUser() {

        //Delete the object user
        firebaseUser.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                mAuth.signOut();
                Toast.makeText(DeleteProfileActivity.this, "User has been deleted!", Toast.LENGTH_LONG).show();
                //Open ProfileFragment Activity and Stop user from returning to ChangePassword Activity on pressing back button and close activity
                Intent intent = new Intent(DeleteProfileActivity.this, Login.class);
                startActivity(intent);
                finish();
            } else {
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (Exception e) {
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    //Delete All data of the user
    private void deleteUserData(FirebaseUser firebaseUser) {

        //Delete Display Pic. Also check if the user has uploaded picture before deleting
        if (firebaseUser.getPhotoUrl() != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference = firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnCompleteListener(task -> {
                Log.d(TAG, "OnSuccess: Photo deleted");
                //Finally deleting the user after deleting the related data
                deleteUser();
            }).addOnFailureListener(e -> {
                Log.d(TAG, "OnSuccess: Photo deleted");
                Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }


        //Delete Data from Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(unused -> Log.d(TAG, "OnSuccess: User Data deleted")).addOnFailureListener(e -> {
            Log.d(TAG, "OnSuccess: User Data deleted");
            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            //Refresh action
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            finish();
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(DeleteProfileActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DeleteProfileActivity.this, Login.class);

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
        Intent intent = NavigationUtils.openProfileFragment(DeleteProfileActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}