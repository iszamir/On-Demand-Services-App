package com.example.customtoolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword;
    private Button buttonLogin, buttonForgotPwd;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private static final String TAG = "Login";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is login, if logged in, this will open mainactivity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();   //Close Login activity
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.signupNow);
        buttonForgotPwd = findViewById(R.id.btn_forgot_password);
        
        //Forgot password function
        buttonForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "You can reset your password now!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, ForgotPasswordActivity.class));
            }
        });

        //Show hide password using Eye icon
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_hideShow_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.hide_password);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPassword.getTransformationMethod() instanceof HideReturnsTransformationMethod) {
                    //If password is visible, this will Hide it.
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Eye icon
                    imageViewShowHidePwd.setImageResource(R.drawable.hide_password);
                } else {
                    //If password is hidden, show it
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.show_password);
                }
            }
        });

        /* To open register activity (click to navigate to register activity) */
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);        // Set progress bar to visible while onclick
                String email, password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // Check email & password field is not empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_SHORT).show();
                    editTextEmail.setError("Valid email is required");
                    editTextEmail.requestFocus();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    //Get instance of current user
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    //Check if email is verified before user can access
                                    if (firebaseUser.isEmailVerified()) {
                                        Toast.makeText(Login.this, "Login successful.", Toast.LENGTH_SHORT).show();
                                        // Open mainActivity
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        firebaseUser.sendEmailVerification();
                                        mAuth.signOut();        //Sign out from app
                                        showAlertDialogue();
                                    }

                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        editTextEmail.setError("Invalid credentials. Kindly check and re-enter");
                                        editTextEmail.requestFocus();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        editTextEmail.setError("User does not exist or no longer valid. Please register again");
                                        editTextEmail.requestFocus();
                                    } catch (Exception e) {
                                        Log.e(TAG, "Exception type: " + e.getClass().getName());
                                        //If sign if fails, display error message using Log
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });

            }
        });
    }

    private void showAlertDialogue() {
        //Setup the Alert Dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can't login without email verification");

        //Open email apps if user clicks Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     //Open email app in new window
                startActivity(intent);
            }
        });

        //Create the Alert Dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}