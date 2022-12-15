package com.example.tabata_timer.utility;

import java.util.ArrayList;

/**
 * Classe proposant un mécanisme d'abonnement auditeur/source
 * En association avec l'interface OnUpdateListener
 */
public class UpdateSource {

    // Liste des auditeurs
    private final ArrayList<OnUpdateListener> listeners = new ArrayList<>();

    private final ArrayList<OnFinishListenner> listenersF = new ArrayList<>();

    // Méthode d'abonnement
    public void addOnUpdateListener(OnUpdateListener listener) {
        listeners.add(listener);
    }

    public void addOnFinishListenner(OnFinishListenner listenner) {
        listenersF.add(listenner);
    }

    // Méthode activée par la source pour prévenir les auditeurs de l'événement update
    public void update() {

        // Notify everybody that may be interested.
        for (OnUpdateListener listener : listeners)
            listener.onUpdate();
    }

    public void finish() {
        for (OnFinishListenner listenner : listenersF)
            listenner.onFinish();
    }
}
