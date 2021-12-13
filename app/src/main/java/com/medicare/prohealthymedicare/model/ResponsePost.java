package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

public class ResponsePost {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    Integer data;

    public ResponsePost(String message, Integer data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Integer getData() {
        return data;
    }
}
