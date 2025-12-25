package com.example.placementproject;

public class DataClass {
    private String edtCompanyName;
    private String edtJobPreference;
    private String edtLink;
    private String edtDate;
    private String key; // To store Firebase unique key

    // Required default constructor for Firebase
    public DataClass() {
    }

    // Constructor with 3 arguments
    public DataClass(String edtJobPreference, String edtCompanyName, String edtLink) {
        this.edtJobPreference = edtJobPreference;
        this.edtCompanyName = edtCompanyName;
        this.edtLink = edtLink;
    }

    // Constructor with 4 arguments (including date)
    public DataClass(String edtJobPreference, String edtCompanyName, String edtLink, String edtDate) {
        this.edtJobPreference = edtJobPreference;
        this.edtCompanyName = edtCompanyName;
        this.edtLink = edtLink;
        this.edtDate = edtDate;
    }

    // Getters
    public String getEdtCompanyName() { return edtCompanyName; }
    public String getEdtJobPreference() { return edtJobPreference; }
    public String getEdtLink() { return edtLink; }
    public String getEdtDate() { return edtDate; }
    public String getKey() { return key; }

    // Setters
    public void setEdtCompanyName(String edtCompanyName) { this.edtCompanyName = edtCompanyName; }
    public void setEdtJobPreference(String edtJobPreference) { this.edtJobPreference = edtJobPreference; }
    public void setEdtLink(String edtLink) { this.edtLink = edtLink; }
    public void setEdtDate(String edtDate) { this.edtDate = edtDate; }
    public void setKey(String key) { this.key = key; }
}
