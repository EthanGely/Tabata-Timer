package com.example.tabata_timer.database.dbSettings;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {

    ////////////////////Attributs/////////////////////////
    @PrimaryKey(autoGenerate = true)
    private long id;

    private boolean isSoundOn = true;

    private int idFavori = -1;


    ///////////////////////////////////////////////////////////////////////


    /////////////////////////-- Constructeur --///////////////////////////
    public Settings() {
    }
    ///////////////////////////////////////////////////////////////////////

    ////////////////////////////GETTEURS / SETTEURS ///////////////////////
    public long getId() {
        return id;
    }

    public boolean getIsSoundOn() {
        return isSoundOn;
    }

    public int getIdFavori() {
        return idFavori;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setSoundOn(boolean soundOn) {
        this.isSoundOn = soundOn;
    }

    public void setIdFavori(int idFavori) {
        this.idFavori = idFavori;
    }
}
