package com.example.tabata_timer.database;



import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciceDao {

    @Query("SELECT * FROM exercice")
    List<Exercice> getAll();

    @Query("SELECT * FROM exercice WHERE id = :id")
    Exercice findExerciceByID(int id);

    @Insert
    long insert(Exercice exercice);

    @Insert
    long[] insertAll(Exercice... exercices);

    @Delete
    void delete(Exercice exercice);

    @Update
    void update(Exercice exercice);

}