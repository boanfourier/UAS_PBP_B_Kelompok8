package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

public class ResponseAuth {
    @SerializedName("uid")
    private String uid;
    @SerializedName("firstname")
    private String firstname;
    @SerializedName("lastname")
    private String lastname;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("foto")
    private String foto;
    @SerializedName("nohp")
    private String nohp;


    public ResponseAuth(String uid, String firstname, String lastname, String email, String password, String foto, String nohp) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.foto = foto;
        this.nohp = nohp;
    }

    public String getUid() {
        return uid;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFoto() {
        return foto;
    }

    public String getNohp() {
        return nohp;
    }
}
