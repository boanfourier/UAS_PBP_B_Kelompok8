package com.medicare.prohealthymedicare.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.medicare.prohealthymedicare.database.entity.UserEntity;


@Dao
public interface UsersDao {

    @Query("SELECT * FROM users where username=(:username)")
    UserEntity cekuser(String username);

    @Query("INSERT INTO users (firstname,lastname,username,password) VALUES (:firstname,:lastname,:username,:password)")
    void insertAuth(String firstname, String lastname, String username, String password);

    @Query("UPDATE users SET  foto=:foto  WHERE username=:username")
    void updatefoto(String foto,String username);

    @Query("UPDATE users SET firstname=:firstname,lastname=:lastname,username=:username, password=:password  WHERE username=:username")
    void updateakun(String firstname,String lastname, String username, String password);


    @Query("SELECT * FROM users where username=(:username) and password=(:password)")
    UserEntity login(String username, String password);
}
