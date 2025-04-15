
package com.example.placementproject;

public class list_of_tracker {
    private int id;
    private String companyname;
    private String jobstext;
    private String status;
    private String appliedtime;
    private String edtlink;

    // Constructor without id
    public list_of_tracker(String companyname, String jobstext, String status, String appliedtime,String edtlink) {
        this.companyname = companyname;
        this.jobstext = jobstext;
        this.status = status;
        this.appliedtime = appliedtime;
        this.edtlink=edtlink;
    }

    // Optional constructor with id
    public list_of_tracker(int id, String companyname, String jobstext, String status, String appliedtime,String edtlink) {
        this.id = id;
        this.companyname = companyname;
        this.jobstext = jobstext;
        this.status = status;
        this.appliedtime = appliedtime;
        this.edtlink = edtlink;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public String getJobstext() {
        return jobstext;
    }

    public String getStatus() {
        return status;
    }

    public String getAppliedtime() {
        return appliedtime;
    }
    public String getEdtlink() {  // Add getter for edtlink
        return edtlink;
    }

    // Setters (Optional, in case you want to modify the fields later)
    public void setEdtlink(String edtlink) {
        this.edtlink = edtlink;
    }
}

