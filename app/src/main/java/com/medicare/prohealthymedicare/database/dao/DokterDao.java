package com.medicare.prohealthymedicare.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;


import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.database.entity.UserEntity;

import java.util.List;

@Dao
public interface DokterDao {

    @Query("INSERT INTO tb_dokter (namadokter, hari,jam, jenis,username,antri,keluhan) VALUES (:namadokter,:hari,:jam,:jenis,:username,:antri,:keluhan)")
    void insertdokter(String namadokter, String hari, String jenis, String username, String jam, Integer antri, String keluhan);

    @Query("SELECT * FROM tb_dokter where username=(:username) AND jenis=:jenis")
    DokterEntity cekdokter(String username, String jenis);

    @Query("SELECT * FROM tb_dokter where username=(:username)")
    List<DokterEntity> getjadwal(String username);

    @Query("UPDATE tb_dokter SET  jam=:jam,keluhan=:keluhan WHERE username=:username AND jenis=:jenis")
    void update(String jam, String keluhan,  String username, String jenis);

    @Delete
    void delete(DokterEntity transaksi);




}
