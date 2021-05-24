package com.opportunitymanagement;

public class Account {
    private String accountId;
    private String accountName;
    private String accountAddress;

    public Account() {
    }

    public Account(String accountId, String accountName, String accountAddress) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.accountAddress = accountAddress;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountAddress() {
        return accountAddress;
    }
}
