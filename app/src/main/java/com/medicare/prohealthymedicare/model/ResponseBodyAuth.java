package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

public class ResponseBodyAuth {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    ResponseAuth data;

    public ResponseBodyAuth(String message, ResponseAuth data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public ResponseAuth getUsersmodel() {
        return data;
    }
}