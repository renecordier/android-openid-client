package com.bip.androidopenidclient.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by rene on 21/03/17.
 */

public class UserInfo {
    @SerializedName("sub")
    private String bipAccountId;
    @SerializedName("email")
    private String email;
    @SerializedName("email_verified")
    private boolean emailVerified;
    @SerializedName("name")
    private String fullName;
    @SerializedName("given_name")
    private String firstName;
    @SerializedName("family_name")
    private String lastName;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("phone_number_verified")
    private boolean phoneNumberVerified;
    @SerializedName("aud")
    private ArrayList<String> aud;

    public String getBipAccountId() {
        return bipAccountId;
    }

    public void setBipAccountId(String bipAccountId) {
        this.bipAccountId = bipAccountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public ArrayList<String> getAud() {
        return aud;
    }

    public void setAud(ArrayList<String> aud) {
        this.aud = aud;
    }
}
