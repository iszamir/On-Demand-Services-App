package com.example.customtoolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextName, editTextDob, editTextMobileNumber,
                      editTextConfirmPassword;
    RadioGroup radioGroupGender;
    RadioButton radioButtonGenderSelect;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    DatePickerDialog picker;
    private  final static String TAG = "Register";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is login, if logged in, this will open mainActivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        editTextName = findViewById(R.id.editText_name);
        editTextDob = findViewById(R.id.editText_dob);
        editTextMobileNumber = findViewById(R.id.editText_mobile_number);
        editTextConfirmPassword =findViewById(R.id.editText_confirm_password);
        buttonReg = findViewById(R.id.btn_signup);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        //Radiobutton for gender
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioGroupGender.clearCheck();

        //Setting up Date Picker on EditText
        editTextDob.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            //Date Picker dialog
            picker = new DatePickerDialog(Register.this, (view, year1, month1, dayOfMonth) -> editTextDob.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
            picker.show();
        });

        // To open login activity (click to login)
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        //Set function for button
        buttonReg.setOnClickListener(v -> {
            String textFullName = String.valueOf(editTextName.getText());
            String textEmail = String.valueOf(editTextEmail.getText());
            String textDob = String.valueOf(editTextDob.getText());
            String textMobile = String.valueOf(editTextMobileNumber.getText());
            String textPassword = String.valueOf(editTextPassword.getText());
            String textConfirmPwd = String.valueOf(editTextConfirmPassword.getText());

            //Pass selected gender value into an object
            int selectedId = radioGroupGender.getCheckedRadioButtonId();
            String textGender = "";

            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                textGender = selectedRadioButton.getText().toString();
            }

            //Validate mobile number using Matcher and Pattern (Regular Expression)
            String mobileRegex = "^01\\d{8}$"; //First no. is 0, second no. is 1, the rest 8 digit can be any number
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(textMobile);


            // Check all input field is not empty nor contains errors.
            int genderId = radioGroupGender.getCheckedRadioButtonId();
            if (TextUtils.isEmpty(textFullName)) {
                Toast.makeText(Register.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                editTextName.setError("Full Name is required");
                editTextName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                Toast.makeText(Register.this, "Please enter your email", Toast.LENGTH_LONG).show();
                editTextEmail.setError("Email is required");
                editTextEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {    //Pattern matching for email input
                Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                editTextEmail.setError("A valid email is required");
                editTextEmail.requestFocus();
            } else if (TextUtils.isEmpty(textDob)) {
                Toast.makeText(Register.this, "Please enter your date of birth", Toast.LENGTH_LONG).show();
                editTextDob.setError("Birth date is required");
                editTextDob.requestFocus();
            } else if (genderId == -1) {
                radioButtonGenderSelect = findViewById(radioGroupGender.getCheckedRadioButtonId());
                Toast.makeText(Register.this, "Please select your gender", Toast.LENGTH_LONG).show();
                if (radioButtonGenderSelect != null ){
                    radioButtonGenderSelect.setError("Your gender is required");
                    radioButtonGenderSelect.requestFocus();
                }
            } else if (TextUtils.isEmpty(textMobile)) {
                Toast.makeText(Register.this, "Please enter your mobile no.", Toast.LENGTH_LONG).show();
                editTextMobileNumber.setError("Mobile No. is required");
                editTextMobileNumber.requestFocus();
            } else if (textMobile.length() != 10) {
                Toast.makeText(Register.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                editTextMobileNumber.setError("Mobile No. should be 10 digits, without - or spaces.");
                editTextMobileNumber.requestFocus();
            } else if (!mobileMatcher.matches()) {
                Toast.makeText(Register.this, "Please re-enter your mobile no.", Toast.LENGTH_LONG).show();
                editTextMobileNumber.setError("Mobile No. is not valid");
                editTextMobileNumber.requestFocus();
            } else if (TextUtils.isEmpty(textPassword)) {
                Toast.makeText(Register.this, "Please enter your password", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Password is required");
                editTextPassword.requestFocus();
            } else if (editTextPassword.length() < 6) {
                Toast.makeText(Register.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                editTextPassword.setError("Password too weak");
                editTextPassword.requestFocus();
            } else if (TextUtils.isEmpty(textConfirmPwd)) {
                Toast.makeText(Register.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                editTextConfirmPassword.setError("Password confirmation is required");
                editTextConfirmPassword.requestFocus();
            } else if (!textPassword.equals(textConfirmPwd)) {
                Toast.makeText(Register.this, "Please use the same password", Toast.LENGTH_LONG).show();
                editTextConfirmPassword.setError("Password confirmation is required");
                editTextConfirmPassword.requestFocus();
                //Clear the entered password
                editTextPassword.clearComposingText();
                editTextConfirmPassword.clearComposingText();
            } else {
                progressBar.setVisibility(View.VISIBLE);        // Set progress bar to visible while onclick
                registerUser(textFullName, textEmail, textDob, textGender, textPassword, textMobile);
            }
        });}

    private void registerUser(String textFullName, String textEmail, String textDob, String textGender,
                              String textPassword, String textMobile) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Create a new account by passing the new user's email address and password to createUserWithEmailAndPassword
        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        //Update display name of the user
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(profileChangeRequest);

                        //Enter user's data into Firebase Realtime Database
                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails (textDob, textGender, textMobile);

                        //Extracting user reference from database for Registered Users
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                        referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(task1 -> {

                            if (task1.isSuccessful()) {

                                //Send a mail to user's email for verification.
                                firebaseUser.sendEmailVerification();

                                Toast.makeText(Register.this, "User registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();

                                //Open login activity, after registration is successful
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                //To prevent user from returning back to Register Activity upon pressing back button from Registration page
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(Register.this, "User registration failed. Please try again", Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        });

                    } else {
                        // If sign in fails, check for specific errors
                        try {
                            if (task.getException() != null) {
                            throw task.getException(); }
                        } catch (FirebaseAuthUserCollisionException e) {
                            // User already exists or email already registered
                            editTextPassword.setError("User is already registered with this email. Use another email or proceed to login");
                            editTextPassword.requestFocus();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            // Password is weak
                            editTextPassword.setError("Your password is too weak. Please use a mix of alphabets, numbers and special charactters");
                            editTextPassword.requestFocus();
                        } catch (Exception e) {
                            String errorMessage = e.getMessage() != null ? e.getMessage() : "An error occurred.";
                            Toast.makeText(Register.this, errorMessage, Toast.LENGTH_LONG).show();
                            Log.e(TAG, e.getMessage());
                            // Other errors
                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}