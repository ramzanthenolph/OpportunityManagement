package com.opportunitymanagement;

import com.google.firebase.database.IgnoreExtraProperties;

public class Opportunity {
    private String opportunityId;
    private String opportunityName;
    private  int stage;

    public Opportunity(){

    }

    public Opportunity(String opportunityId, String opportunityName, int stage) {
        this.opportunityId = opportunityId;
        this.opportunityName = opportunityName;
        this.stage = stage;
    }

    public String getOpportunityName() {
        return opportunityName;
    }

    public int getStage() {
        return stage;
    }
}
