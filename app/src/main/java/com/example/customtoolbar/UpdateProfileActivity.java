package com.example.customtoolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDob, editTextUpdateMobileNo;
    private RadioButton radioButtonUpdateGenderSelected;
    private RadioGroup radioGroupUpdateGender;
    private String textFullName, textGender, textDob, textMobile;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Upload Profile Details");
        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDob = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobileNo = findViewById(R.id.editText_update_profile_mobile);
        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        //Show Profile method
        assert firebaseUser != null;
        showProfile(firebaseUser);

        //Upload Profile Pic method
        Button buttonUpdateProfilePic = findViewById(R.id.btn_update_profile_pic);
        buttonUpdateProfilePic.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePictureActivity.class);
            startActivity(intent);
            finish();
        });

        //Update email method
        Button btnUpdateEmail = findViewById(R.id.btn_update_email);
        btnUpdateEmail.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        });

        //Setting up Date Picker on EditText
        editTextUpdateDob.setOnClickListener(v -> {

            //Extracting saved dd/mm/yyyy into different variables by creating an array delimited by "/"
            String[] textSADoB = textDob.split("/");

            int day = Integer.parseInt(textSADoB[0]);
            int month = Integer.parseInt(textSADoB[1]) - 1;
            int year = Integer.parseInt(textSADoB[2]);

            DatePickerDialog picker;
            //Date Picker dialog
            picker = new DatePickerDialog(UpdateProfileActivity.this, (view, year1, month1, dayOfMonth) -> editTextUpdateDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        //Update Profile method
        Button buttonUpdateProfile = findViewById(R.id.btn_update_profile);
        buttonUpdateProfile.setOnClickListener(v -> updateProfile(firebaseUser));

    }


    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting User Reference from database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    textFullName = firebaseUser.getDisplayName();
                    textDob = readUserDetails.doB;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDob.setText(textDob);
                    editTextUpdateMobileNo.setText(textMobile);

                    //Show Gender through Radio Button
                    if (textGender.equals("male")) {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    } else {
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                } else {
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong",
                            Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong",
                        Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //Fetch data from database and display
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
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            mAuth.signOut();
            Toast.makeText(UpdateProfileActivity.this, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UpdateProfileActivity.this, Login.class);

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


    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        //Validate mobile number using Matcher and Pattern (Regular Expression)
        String mobileRegex = "^01\\d{8}$"; //First no. is 0, second no. is 1, the rest 8 digit can be any number
        Pattern mobilePattern = Pattern.compile(mobileRegex);
        Matcher mobileMatcher = mobilePattern.matcher(textMobile);


        // Check all input field is not empty nor contains errors.
        if (TextUtils.isEmpty(textFullName)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            editTextUpdateName.setError("Full Name is required");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textDob)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
            editTextUpdateDob.setError("Birth date is required");
            editTextUpdateDob.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(UpdateProfileActivity.this, "Please select your gender", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Your gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(UpdateProfileActivity.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobileNo.setError("Mobile No. is required");
            editTextUpdateMobileNo.requestFocus();
        } else if (textMobile.length() != 10) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobileNo.setError("Mobile No. should be 10 digits, without - or spaces.");
            editTextUpdateMobileNo.requestFocus();
        } else if (!mobileMatcher.matches()) {
            Toast.makeText(UpdateProfileActivity.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
            editTextUpdateMobileNo.setError("Mobile No. is not valid");
            editTextUpdateMobileNo.requestFocus();
        } else {
            //Obtain data entered by user
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDob = editTextUpdateDob.getText().toString();
            textMobile = editTextUpdateMobileNo.getText().toString();

            //Enter User data into Firebase Realtime Database. Set up dependencies.
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDob, textGender, textMobile);

            //Extract User reference from Database for 'Registered Users'
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    //Setting new display name
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().
                            setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    Toast.makeText(UpdateProfileActivity.this, "Update Successful!", Toast.LENGTH_SHORT).show();

                    //Stop user from returning to UpdateProfileActivity on pressing back button and close activity
                    Intent intent = NavigationUtils.openProfileFragment(UpdateProfileActivity.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            });
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = NavigationUtils.openProfileFragment(UpdateProfileActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }




}

