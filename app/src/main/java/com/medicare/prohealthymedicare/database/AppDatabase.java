package com.medicare.prohealthymedicare.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.medicare.prohealthymedicare.database.dao.DokterDao;
import com.medicare.prohealthymedicare.database.dao.UsersDao;
import com.medicare.prohealthymedicare.database.entity.DokterEntity;
import com.medicare.prohealthymedicare.database.entity.UserEntity;

import org.jetbrains.annotations.NotNull;

@Database(entities = {UserEntity.class, DokterEntity.class} ,  version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;
    private final MutableLiveData<Boolean> mIsdatabaseCreated = new MutableLiveData<>();

    @VisibleForTesting
    public static final String DATABASE_NAME = "databaseku";

    public abstract UsersDao userDao();
    public abstract DokterDao dokterDao();
//    public abstract LaptopDao laptopDao();


    private void setDatabaseCreated(){
        mIsdatabaseCreated.postValue(true);
    }

    private void updateDatabaseCreated(final Context context){
        if (context.getDatabasePath(DATABASE_NAME).exists()){
            setDatabaseCreated();
        }
    }

    public static AppDatabase buildDatabase(final Context context){
        return Room.databaseBuilder(context,AppDatabase.class,DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull @NotNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        AppDatabase database = AppDatabase.getInstance(context);
                        database.setDatabaseCreated();
                    }
                }).allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }

    public static AppDatabase getInstance(final Context context){
        if (sInstance == null){
            synchronized (AppDatabase.class){
                sInstance = buildDatabase(context);
                sInstance.updateDatabaseCreated(context.getApplicationContext());
            }
        }
        return sInstance;
    }
}
