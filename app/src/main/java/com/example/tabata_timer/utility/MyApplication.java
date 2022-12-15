package com.example.tabata_timer.utility;

import android.app.Application;
import android.media.MediaPlayer;

import com.example.tabata_timer.R;

public class MyApplication extends Application {

    private MediaPlayer soundEffect;

    private Compteur compteur;

    private int volume;


    public void setSoundEffect(MediaPlayer soundEffect, int volume) {
        this.soundEffect = soundEffect;
        setVolume(volume);
    }

    public void setVolume(int volume) {
        this.volume = volume;
        this.soundEffect.setVolume(volume, volume);
    }

    public void playLetsGo() {
        soundEffect = MediaPlayer.create(MyApplication.this, R.raw.lets_go);
        soundEffect.setVolume(volume, volume);
        soundEffect.start();
    }

    public void playSifflet() {
        soundEffect = MediaPlayer.create(MyApplication.this, R.raw.sifflet);
        soundEffect.setVolume(volume, volume);
        soundEffect.start();
    }

    public void playAlmostThere() {
        soundEffect = MediaPlayer.create(MyApplication.this, R.raw.almost_end);
        soundEffect.setVolume(volume, volume);
        soundEffect.start();
    }

    public void playRestart() {
        soundEffect = MediaPlayer.create(MyApplication.this, R.raw.restart);
        soundEffect.setVolume(volume, volume);
        soundEffect.start();
    }

    public void playEnd() {
        soundEffect = MediaPlayer.create(MyApplication.this, R.raw.end_sound);
        soundEffect.setVolume(volume, volume);
        soundEffect.start();
    }

    public void stopSound() {
        if (soundEffect != null) {
            soundEffect.stop();
            soundEffect.reset();
        }
        soundEffect = new MediaPlayer();
    }

    public void setCompteur(Compteur compteur) {
        this.compteur = compteur;
    }

    public void stopCompteur() {
        if (compteur != null) {
            compteur.stop();
        }
        compteur = null;
    }
}
