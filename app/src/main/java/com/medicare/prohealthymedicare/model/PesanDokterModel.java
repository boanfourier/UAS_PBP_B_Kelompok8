package com.medicare.prohealthymedicare.model;

import com.google.gson.annotations.SerializedName;

public class PesanDokterModel {
    @SerializedName("id")
    Integer id;
    @SerializedName("nama")
    String nama;
    @SerializedName("hari")
    String hari;
    @SerializedName("jam")
    String jam;
    @SerializedName("jenis")
    String jenis;
    @SerializedName("uid")
    String uid;
    @SerializedName("antri")
    Integer antri;
    @SerializedName("keluhan")
    String keluhan;

    public PesanDokterModel(Integer id, String nama, String hari, String jam, String jenis, String uid, Integer antri, String keluhan) {
        this.id = id;
        this.nama = nama;
        this.hari = hari;
        this.jam = jam;
        this.jenis = jenis;
        this.uid = uid;
        this.antri = antri;
        this.keluhan = keluhan;
    }

    public Integer getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getHari() {
        return hari;
    }

    public String getJam() {
        return jam;
    }

    public String getJenis() {
        return jenis;
    }

    public String getUid() {
        return uid;
    }

    public Integer getAntri() {
        return antri;
    }

    public String getKeluhan() {
        return keluhan;
    }
}
