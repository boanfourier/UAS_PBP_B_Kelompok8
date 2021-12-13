package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponsePesan {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    List<PesanDokterModel> pesanDokterModels;

    public ResponsePesan(String message, List<PesanDokterModel> pesanDokterModels) {
        this.message = message;
        this.pesanDokterModels = pesanDokterModels;
    }

    public String getMessage() {
        return message;
    }

    public List<PesanDokterModel> getPesanDokterModels() {
        return pesanDokterModels;
    }
}
