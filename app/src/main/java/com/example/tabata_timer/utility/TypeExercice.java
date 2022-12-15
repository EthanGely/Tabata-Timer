package com.example.tabata_timer.utility;

import androidx.annotation.NonNull;

public enum TypeExercice {
    MULTISPORT("Multi-sport"), POMPES("Pompes"), TRACTIONS("Tractions"), REPOS("Repos"), JUMPIN_JACKS("Jumpin Jacks"), SQUATS("Squats");

    private final String name;

    TypeExercice(String nom) {
        this.name = nom;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
