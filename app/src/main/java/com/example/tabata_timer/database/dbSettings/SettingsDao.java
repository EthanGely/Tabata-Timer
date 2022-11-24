package com.example.tabata_timer.database.dbSettings;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SettingsDao {

    @Query("SELECT * FROM settings")
    List<Settings> getAll();

    @Query("SELECT * FROM settings WHERE id = 1")
    Settings getSettings();

    @Insert
    long insert(Settings settings);

    @Insert
    long[] insertAll(Settings... settings);

    @Delete
    void delete(Settings settings);

    @Update
    void update(Settings settings);

}