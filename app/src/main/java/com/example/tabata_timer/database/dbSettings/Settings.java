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

    public int getVolume() {
        return volume;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSoundOn(boolean soundOn) {
        this.isSoundOn = soundOn;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }


}
