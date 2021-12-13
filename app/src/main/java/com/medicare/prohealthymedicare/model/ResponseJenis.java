package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseJenis {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    List<JenisModel> jenisModels;

    public ResponseJenis(String message, List<JenisModel> jenisModels) {
        this.message = message;
        this.jenisModels = jenisModels;
    }

    public String getMessage() {
        return message;
    }

    public List<JenisModel> getJenisModels() {
        return jenisModels;
    }
}
