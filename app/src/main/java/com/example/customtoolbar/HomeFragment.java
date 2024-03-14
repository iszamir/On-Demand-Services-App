package com.example.customtoolbar;

import static com.example.customtoolbar.R.id.button_post_service;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeFragment extends Fragment {

    private OnBackPressedListener onBackPressedListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Button logoutButton = view.findViewById(R.id.btnlogout);
        Button postServiceButton = view.findViewById(button_post_service);
        CardView cvCleaner = view.findViewById(R.id.cv_cleaner);
        CardView cvPlumber = view.findViewById(R.id.cv_plumber);
        CardView cvContractor = view.findViewById(R.id.cv_contractor);
        CardView cvMover = view.findViewById(R.id.cv_mover);
        CardView cvDispatch = view.findViewById(R.id.cv_dispatch);
        CardView cvBabysitter = view.findViewById(R.id.cv_babysitter);
        CardView cvMaid = view.findViewById(R.id.cv_maid);
        CardView cvShopper = view.findViewById(R.id.cv_shopper);
        CardView cvPhotographer = view.findViewById(R.id.cv_photographer);
        CardView cvProgrammer = view.findViewById(R.id.cv_programmer);
        CardView cvDesigner = view.findViewById(R.id.cv_designer);
        CardView cvAnimation = view.findViewById(R.id.cv_animation);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }

        //Logout from apps
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireActivity().getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
        postServiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity().getApplicationContext(), PostServicesActivity.class);
            startActivity(intent);
            finish();
        });

        cvCleaner.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvPlumber.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvContractor.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvMover.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvDispatch.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvBabysitter.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvMaid.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvShopper.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvPhotographer.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvProgrammer.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvDesigner.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        cvAnimation.setOnClickListener(v -> {
            Intent intent  = new Intent(requireActivity().getApplicationContext(), ServicesListActivity.class);
            startActivity(intent);
            finish();
        });

        return view;
    }

    private void finish() {
    }

    public static HomeFragment newInstance () {
        return new HomeFragment();
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
            onBackPressedListener.onHomeBackButtonPressed();
        }
    }


    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener {
        void onHomeBackButtonPressed();
    }

}

