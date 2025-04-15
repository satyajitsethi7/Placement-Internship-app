
package com.example.placementproject;

public class DataClass {
    private String edtCompanyName;
    private String edtJobPreference;
    private String edtLink;
    private String appliedDate;

    // Required default constructor for Firebase
    public DataClass() {
    }

    // Constructor (optional, can be used when uploading data)
    public DataClass(String edtJobPreference, String edtCompanyName, String edtLink, String appliedDate) {
        this.edtCompanyName = edtCompanyName;
        this.edtJobPreference = edtJobPreference;
        this.edtLink = edtLink;
        this.appliedDate = appliedDate;
    }

    // Getters (important for Firebase to read values)
    public String getEdtCompanyName() {
        return edtCompanyName;
    }

    public String getEdtJobPreference() {
        return edtJobPreference;
    }

    public String getEdtLink() {
        return edtLink;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    // Setters (important for Firebase to map values)
    public void setEdtCompanyName(String edtCompanyName) {
        this.edtCompanyName = edtCompanyName;
    }

    public void setEdtJobPreference(String edtJobPreference) {
        this.edtJobPreference = edtJobPreference;
    }

    public void setEdtLink(String edtLink) {
        this.edtLink = edtLink;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }
}


