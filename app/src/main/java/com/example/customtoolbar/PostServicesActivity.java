package com.example.customtoolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostServicesActivity extends AppCompatActivity {
    private String selectedServices, selectedState, selectedDistrict;                     //Variables to hold values of selected service and state
    private TextView textViewServices, textViewState, textViewDistrict;                   // TV to display errors
    private Spinner servicesSpinner, stateSpinner, districtSpinner;
    private ArrayAdapter<CharSequence> districtAdapter;    //Only declaration
    TextInputEditText editTextTitle, editTextDescription, editTextPhoneNumber, editTextPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_services);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Post Your Service");
        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        //Spinner Initialisation
        servicesSpinner = findViewById(R.id.spinner_service_type);
        stateSpinner = findViewById(R.id.spinner_service_state);
        //Define the district spinner and populate the options through the selected state
        districtSpinner = findViewById(R.id.spinner_service_district);

        //Populate ArrayAdapter using string array and a spinner layout that is defined
        ArrayAdapter<CharSequence> serviceAdapter = ArrayAdapter.createFromResource(this, R.array.array_services,
                R.layout.spinner_layout);
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.array_state,
                R.layout.spinner_layout);

        //Specify of layout to use when list of choices appear
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set the adapter to the spinner to populate the Service Spinner
        servicesSpinner.setAdapter(serviceAdapter);
        stateSpinner.setAdapter(stateAdapter);

        //Set selection to state spinner so that district spinner border is visible
        stateSpinner.setSelection(1);

        servicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedServices = servicesSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //When any item of State Spinner is selected
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "Selected state: " + selectedState);

                //Obtain the selected state
                selectedState = stateSpinner.getSelectedItem().toString();

                int parentID = parent.getId();
                if (parentID == R.id.spinner_service_state) {
                    switch (selectedState) {
                        case "Select your State":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_default_districts, R.layout.spinner_layout);
                            break;
                        case "Johor":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_johor_districts, R.layout.spinner_layout);
                            break;
                        case "Kedah":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_kedah_districts, R.layout.spinner_layout);
                            break;
                        case "Kelantan":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_kelantan_districts, R.layout.spinner_layout);
                            break;
                        case "Melaka":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_melaka_districts, R.layout.spinner_layout);
                            break;
                        case "Negeri Sembilan":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_negeri_sembilan_districts, R.layout.spinner_layout);
                            break;
                        case "Pahang":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_pahang_districts, R.layout.spinner_layout);
                            break;
                        case "Perak":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_perak_districts, R.layout.spinner_layout);
                            break;
                        case "Perlis":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_perlis_districts, R.layout.spinner_layout);
                            break;
                        case "Pulau Pinang":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_penang_districts, R.layout.spinner_layout);
                            break;
                        case "Sabah":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_sabah_districts, R.layout.spinner_layout);
                            break;
                        case "Sarawak":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_sarawak_districts, R.layout.spinner_layout);
                            break;
                        case "Selangor":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_selangor_districts, R.layout.spinner_layout);
                            break;
                        case "Terengganu":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_terengganu_districts, R.layout.spinner_layout);
                            break;
                        case "WP Kuala Lumpur":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_wp_kualalumpur_districts, R.layout.spinner_layout);
                            break;
                        case "WP Labuan":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_wp_labuan_districts, R.layout.spinner_layout);
                            break;
                        case "WP Putrajaya":
                            districtAdapter = ArrayAdapter.createFromResource
                                    (parent.getContext(), R.array.array_wp_putrajaya_districts, R.layout.spinner_layout);
                            break;

                        default:
                            break;
                    }
                    Log.d("TAG", "districtAdapter: " + districtAdapter);

                    if (districtAdapter != null) {
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    }

                    //Populate the Districts according to the Selected State
                    districtSpinner.setAdapter(districtAdapter);

                    //To obtain the selected District from the districtSpinner
                    districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedDistrict = districtSpinner.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextTitle = findViewById(R.id.editText_title_services);
        editTextDescription = findViewById(R.id.editText_description_services);
        editTextPhoneNumber = findViewById(R.id.editText_phone_services);
        editTextPrice = findViewById(R.id.editText_price_services);

        textViewServices = findViewById(R.id.textView_service_type);
        textViewDistrict = findViewById(R.id.textView_services_district);
        textViewState = findViewById(R.id.textView_services_state);
        Button postServiceButton = findViewById(R.id.button_post_service);

        postServiceButton.setOnClickListener(v -> {

            String textTitle = String.valueOf(editTextTitle.getText());
            String textDescription = String.valueOf(editTextDescription.getText());
            String textPhone = String.valueOf(editTextPhoneNumber.getText());
            String textPrice = String.valueOf(editTextPrice.getText());

            // Validate mobile number (can be refactored into a separate method)
            String mobileRegex = "^01\\d{8}$"; //First no. is 0, second no. is 1, the rest 8 digit can be any number
            Pattern mobilePattern = Pattern.compile(mobileRegex);
            Matcher mobileMatcher = mobilePattern.matcher(textPhone);

            // All fields must be non-empty for successful saving
            if (TextUtils.isEmpty(selectedServices) ||
                    "Select Your Service".equals(selectedServices) ||
                    TextUtils.isEmpty(textTitle) ||
                    TextUtils.isEmpty(textDescription) ||
                    TextUtils.isEmpty(textPhone) ||
                    textPhone.length() != 10 ||
                    !mobileMatcher.matches() ||
                    TextUtils.isEmpty(textPrice) ||
                    "Select Your State".equals(selectedState) ||
                    "Select Your District".equals(selectedDistrict)) {

                // Display specific error messages for each empty field
                String errorMessage = "";
                if (TextUtils.isEmpty(selectedServices) || "Select Your Service".equals(selectedServices)) {
                    errorMessage += "Please select a service type.\n";
                    textViewServices.setError("Service is required");
                    textViewServices.requestFocus();
                } if (TextUtils.isEmpty(textTitle)) {
                    errorMessage += "Please enter a title for your service. \n";
                    editTextTitle.setError("Title is required");
                    editTextTitle.requestFocus();
                } if (TextUtils.isEmpty(textDescription)) {
                    errorMessage += "Please provide a description for your service. \n";
                    editTextDescription.setError("Description is required");
                    editTextDescription.requestFocus();
                } if ("Select Your State".equals(selectedState)) {
                    errorMessage += "Please select a state. \n";
                    textViewState.setError("State is required");
                    textViewState.requestFocus();
                }  if ("Select Your District".equals(selectedDistrict)) {
                    errorMessage += "Please select a district. \n";
                    textViewDistrict.setError("District is required");
                    textViewDistrict.requestFocus();
                } if (TextUtils.isEmpty(textPrice)) {
                    errorMessage += "Please enter a price in RM, with or without decimals. \n";
                    editTextPrice.setError("Price is required");
                    editTextPrice.requestFocus();
                } if (TextUtils.isEmpty(textPhone)) {
                    errorMessage += "Please enter a Mobile number \n";
                    editTextPhoneNumber.setError("Mobile Number is required");
                    editTextPhoneNumber.requestFocus();
                } if (textPhone.length() != 10) {
                    errorMessage += "Please enter a valid Mobile number \n";
                    editTextPhoneNumber.setError("A valid Mobile Number is required");
                    editTextPhoneNumber.requestFocus();
                } else if (!mobileMatcher.matches()) {
                    errorMessage += "Please enter a valid Mobile number \n";
                    editTextPhoneNumber.setError("A valid Mobile Number is required");
                    editTextPhoneNumber.requestFocus();
                }


                if (!errorMessage.isEmpty()) {
                    Toast.makeText(PostServicesActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
                return; // Exit the method if any field is empty
            }

            // Call the createServices method to save data
            createServices();
        });

    }

    private void createServices() {

        String textTitle = String.valueOf(editTextTitle.getText());
        String textDescription = String.valueOf(editTextDescription.getText());
        String textPhone = String.valueOf(editTextPhoneNumber.getText());
        String textPrice = String.valueOf(editTextPrice.getText());

        Service service = new Service(textTitle, textDescription, textPhone, textPrice, selectedServices, selectedState, selectedDistrict);

        FirebaseDatabase.getInstance().getReference("UserServices").child(textTitle)
                .setValue(service).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(PostServicesActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(e -> Toast.makeText(PostServicesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = HomeNavigationUtils.openHomeFragment(PostServicesActivity.this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }


}