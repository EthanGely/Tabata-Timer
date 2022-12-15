package com.example.tabata_timer.utility;

import android.os.CountDownTimer;

/**
 * Created by fbm on 24/10/2017.
 */
public class Compteur extends UpdateSource {

    // CONSTANTE
    private final static long INITIAL_TIME = 5000;

    private boolean isStarted = false;

    // DATA
    private long updatedTime = INITIAL_TIME;
    private CountDownTimer timer;   // https://developer.android.com/reference/android/os/CountDownTimer.html


    public Compteur() {
    }

    public void setTimer(long temps) {
        this.updatedTime = temps;
    }

    public boolean getIsStarted() {
        return isStarted;
    }

    // Lancer le compteur
    public void start() {

        if (timer == null) {

            // Créer le CountDownTimer
            isStarted = true;
            timer = new CountDownTimer(updatedTime, 10) {

                // Callback fired on regular interval
                public void onTick(long millisUntilFinished) {
                    updatedTime = millisUntilFinished;

                    // Mise à jour
                    update();
                }

                // Callback fired when the time is up
                public void onFinish() {
                    updatedTime = 0;

                    // Mise à jour
                    update();
                    finish();
                }

            }.start();   // Start the countdown
        }

    }

    // Mettre en pause le compteur
    public void pause() {

        if (timer != null) {
            isStarted = false;
            // Arreter le timer
            stop();

            // Mise à jour
            update();
        }
    }


    // Arrete l'objet CountDownTimer et l'efface
    public void stop() {
        isStarted = false;
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    public int getMinutes() {
        return (int) (updatedTime / 1000) / 60;
    }

    public int getSecondes() {
        int secs = (int) (updatedTime / 1000);
        return secs % 60;
    }

    public int getMillisecondes() {
        return (int) (updatedTime % 1000);
    }

    public int getRemainingMilis() {
        return (int) updatedTime;
    }

}
