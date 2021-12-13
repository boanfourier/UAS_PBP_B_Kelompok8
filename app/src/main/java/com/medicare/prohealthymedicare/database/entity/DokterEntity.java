package com.medicare.prohealthymedicare.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_dokter")

public class DokterEntity {
    @PrimaryKey
    public int id;
    @ColumnInfo(name = "namadokter")
    public String namadokter;
    @ColumnInfo(name = "hari")
    public String hari;
    @ColumnInfo(name = "jam")
    public String jam;
    @ColumnInfo(name = "jenis")
    public String jenis;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "antri")
    public Integer antri;
    @ColumnInfo(name = "keluhan")
    public String keluhan;



}
