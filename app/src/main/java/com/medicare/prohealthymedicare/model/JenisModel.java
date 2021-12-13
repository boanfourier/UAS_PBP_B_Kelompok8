package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

public class JenisModel {
    @SerializedName("jenis")
    String jenis;
    @SerializedName("nama")
    String nama;

    public JenisModel(String jenis, String nama) {
        this.jenis = jenis;
        this.nama = nama;
    }

    public String getJenis() {
        return jenis;
    }

    public String getNama() {
        return nama;
    }
}
