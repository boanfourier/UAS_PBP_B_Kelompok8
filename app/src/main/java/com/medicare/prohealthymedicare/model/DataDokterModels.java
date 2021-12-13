package com.medicare.prohealthymedicare.model;

public class DataDokterModels {

    private String nama, jenis,hari,jam,foto,cp;
    private Integer id;

    public DataDokterModels(String nama, String jenis, String hari, String jam, String foto, String cp, Integer id) {
        this.nama = nama;
        this.jenis = jenis;
        this.hari = hari;
        this.jam = jam;
        this.foto = foto;
        this.cp = cp;
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public String getJenis() {
        return jenis;
    }

    public String getHari() {
        return hari;
    }

    public String getJam() {
        return jam;
    }

    public String getFoto() {
        return foto;
    }

    public String getCp() {
        return cp;
    }

    public Integer getId() {
        return id;
    }
}