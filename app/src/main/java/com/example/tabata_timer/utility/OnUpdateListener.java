package com.example.tabata_timer.utility;

/**
 * Interface intervenant dans le mécanisme d'abonnement auditeur/source
 * En association avec la classe UpdateSource
 */
public interface OnUpdateListener {

    // Méthode appelée à chaque update de l'objet de type UpdateSource (après abonnement)
    public void onUpdate();

}

