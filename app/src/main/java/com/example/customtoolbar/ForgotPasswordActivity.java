package com.example.customtoolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editTextResetPwd;
    private ProgressBar progressBar;
    private static final String TAG = "ForgotPasswordActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        editTextResetPwd = findViewById(R.id.editText_reset_email);
        Button resetPwdButton = findViewById(R.id.button_password_reset);
        progressBar = findViewById(R.id.progressBar);

        resetPwdButton.setOnClickListener(v -> {
            String email = editTextResetPwd.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                editTextResetPwd.setError("Email is require!");
                editTextResetPwd.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(ForgotPasswordActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                editTextResetPwd.setError("Valid email is required");
                editTextResetPwd.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this, "If the email is registered, a link will be sent",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);

                //Clear stack to prevent user coming back to Home Fragment/MainActivity after Logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                // Generic error message for all cases, including unregistered email
                String errorMessage = "An error occurred. Please try again later.";
                editTextResetPwd.setError(errorMessage);
                Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                // Log the error for debugging
                Log.e(TAG, Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                }
            progressBar.setVisibility(View.GONE);
            });
        }
    }