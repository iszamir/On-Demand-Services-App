package com.example.customtoolbar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Service {

    private String title;
    private String description;
    private String phoneNumber;
    private String price;
    private String selectedService;
    private String selectedState;
    private String selectedDistrict;

    public Service () {
        //Default constructor required for calls to DataSnapshot.getValue(Service.class)
    }

    public Service( String title, String description, String phoneNumber, String price,
                   String selectedService, String selectedState, String selectedDistrict) {

        this.title = title;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.price = price;
        this.selectedService = selectedService;
        this.selectedState = selectedState;
        this.selectedDistrict = selectedDistrict;
    }


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return  description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPrice() {
        return price;
    }

    public String getSelectedService() {
        return selectedService;
    }

    public String getSelectedState() {
        return selectedState;
    }

    public String getSelectedDistrict() {
        return selectedDistrict;
    }
}

