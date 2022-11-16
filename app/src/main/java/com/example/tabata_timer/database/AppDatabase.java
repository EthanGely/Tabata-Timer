package com.example.tabata_timer.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Exercice.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ExerciceDao exerciceDao();

}