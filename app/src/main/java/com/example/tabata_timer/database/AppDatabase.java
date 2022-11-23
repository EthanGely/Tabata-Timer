package com.example.tabata_timer.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.database.dbExercices.ExerciceDao;
import com.example.tabata_timer.database.dbSettings.Settings;
import com.example.tabata_timer.database.dbSettings.SettingsDao;

@Database(entities = {Exercice.class, Settings.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExerciceDao exerciceDao();

    public abstract SettingsDao settingsDao();

}