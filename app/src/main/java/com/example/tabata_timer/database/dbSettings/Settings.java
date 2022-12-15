package com.example.tabata_timer.database.dbSettings;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {

    ////////////////////Attributs/////////////////////////
    @PrimaryKey(autoGenerate = true)
    private long id;

    private boolean isSoundOn = true;

    private int volume = 100;

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

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsSoundOn() {
        return isSoundOn;
    }

    public int getIdFavori() {
        return idFavori;
    }

    public void setIdFavori(int idFavori) {
        this.idFavori = idFavori;
    }

    public void setSoundOn(boolean soundOn) {
        this.isSoundOn = soundOn;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVol() {
        return isSoundOn ? 100 : 0;
    }
}
