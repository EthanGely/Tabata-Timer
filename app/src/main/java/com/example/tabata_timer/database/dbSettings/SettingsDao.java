package com.example.tabata_timer.database.dbSettings;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM settings WHERE id = 1")
    Settings getSettings();

    @Insert
    void insert(Settings settings);

    @Update
    void update(Settings settings);

}