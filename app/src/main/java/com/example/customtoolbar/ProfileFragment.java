package com.example.customtoolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ProfileFragment extends Fragment {

    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDob, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender, mobileNumber;
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeContainer;
    private OnBackPressedListener onBackPressedListener;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewWelcome = inflatedView.findViewById(R.id.textView_welcome_msg);
        textViewFullName = inflatedView.findViewById(R.id.textView_show_fullName);
        textViewEmail = inflatedView.findViewById(R.id.textView_show_email);
        textViewDob = inflatedView.findViewById(R.id.textView_show_dob);
        textViewGender = inflatedView.findViewById(R.id.textView_show_gender);
        textViewMobile = inflatedView.findViewById(R.id.textView_show_mobile);
        progressBar = inflatedView.findViewById(R.id.progressBar);

        // Inside your fragment class

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar); // Access Toolbar from activity's view
        if (toolbar != null) {
            ((AppCompatActivity)requireActivity()).setSupportActionBar(toolbar); // Set action bar
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("My Profile"); // Set title
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        }

        //Swipe refresh
        swipeContainer = inflatedView.findViewById(R.id.swipe_refresh_container);
        swipeToRefresh();


        //Set OnClickListener to ImageView
        imageView = inflatedView.findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UploadProfilePictureActivity.class);
            startActivity(intent);
        });

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(getActivity(), "Something went wrong! User details aren't available at the moment", Toast.LENGTH_SHORT).show();
        } else {
            checkIfEmailIsVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        //Retrieve imageUri
        Bundle bundle = getArguments();
        if (bundle != null) {
            Uri imageUri = bundle.getParcelable("imageUri");
            Picasso.get().load(imageUri).into(imageView);
        }

        // Inflate the layout for this fragment
        return inflatedView;
    }

    private void swipeToRefresh() {
        swipeContainer.setOnRefreshListener(() -> {
            // Reload the user profile data
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            if (firebaseUser != null) {
                showUserProfile(firebaseUser);
            } else {
                Toast.makeText(getActivity(), "Something went wrong! Please log in and try again", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            }
            swipeContainer.setRefreshing(false);
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }


    //Users coming to Profile Activity after successful registration
    private void checkIfEmailIsVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Setup Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can't login without email verification next time");

        //Open email apps if user clicks Continue button
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     //Open email app in new window
            startActivity(intent);
        });

        //Create the Alert Dialogue
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobileNumber = readUserDetails.mobile;
                    textViewWelcome.setText("Welcome," + " " + fullName + " " + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDob.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobileNumber);

                    // Load the user's profile picture using Picasso
                    Uri photoUri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(photoUri).into(imageView);

                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    public static ProfileFragment newInstance () {
        return new ProfileFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBackPressed(); // Call your method to handle back button press
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void handleBackPressed() {
        if (onBackPressedListener != null) {
            onBackPressedListener.onProfileBackButtonPressed();
        }
    }

    public void setOnBackPressedListener(OnBackPressedListener listener) {
        onBackPressedListener = listener;
    }

    public interface OnBackPressedListener {
        void onProfileBackButtonPressed();
    }





}