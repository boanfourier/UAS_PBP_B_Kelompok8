package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseBodyDokter {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    List<DataDokterModels> dataDokterModels;

    public ResponseBodyDokter(String message, List<DataDokterModels> dataDokterModels) {
        this.message = message;
        this.dataDokterModels = dataDokterModels;
    }

    public String getMessage() {
        return message;
    }

    public List<DataDokterModels> getDataDokterModels() {
        return dataDokterModels;
    }
}
