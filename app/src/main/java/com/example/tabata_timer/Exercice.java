package com.example.tabata_timer;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Exercice {

    ////////////////////Attributs//////////////
    //Nom de l'exercice
    private String nomExercice;

    private int sport, repos, repetitions, reposLong, seances;


    //Date de dernière modification/lancement
    private Date lastModified;

    //Données sur l'état (variables ci-dessus)
    private ArrayList<Integer> state = new ArrayList<>();


    ////////////////-- Constructeur --//////////////
    public Exercice(String nomExercice, int tempsDeSport, int tempsDeRepos, int nombreDeRepetitions, int reposLong, int nombreDeSeances) {

        //On ajoute le nom de l'exercice et ...
        // ...les valeurs dans la map (la date de modif sera mise à jour automatiquement)
        modifierExercice(nomExercice, tempsDeSport, tempsDeRepos, nombreDeRepetitions, reposLong, nombreDeSeances);


        //L'exercice n'a jamais été lancé (modification automatique de la date)
        saveProgression(0, 0);

    }

    ////////-- Getteur nom exercice (classique) --///////
    public String getNomExercice() {
        return nomExercice;
    }
    /////////////////////////////////////////////////////


    ///-- Setteurs pour les temps d'exercice, de repos..., puis ajout dans l'array --///

    public void modifierExercice(String nomExercice, int tempsDeSport, int tempsDeRepos, int nombreDeRepetitions, int reposLong, int nombreDeSeances) {
        this.nomExercice = nomExercice;
        this.sport = tempsDeSport;
        this.repos = tempsDeRepos;
        this.repetitions = nombreDeRepetitions;
        this.reposLong = reposLong;
        this.seances = nombreDeSeances;
        modificationDate();
    }
    /////////////////////////////////////////////////////////////////


    private String getHourMinTime(int secondes) {
        int hours = (int) secondes / 3600;
        int remaining = (int) secondes - hours * 3600;
        int mins = remaining / 60;
        remaining = remaining - mins * 60;
        String temps = "";

        if (hours > 0) {
            temps = hours + "h ";
        }
        if (mins > 0) {
            temps += mins + "mn ";
        }

        if (remaining > 0) {
            temps += remaining + "s";
        }
        return temps;
    }

    //////////-- getteur pour les valeurs d'exercice --/////////////

    public String getSport() {

        return getHourMinTime(this.sport);
    }

    public String getRepos() {
        return getHourMinTime(this.repos);
    }

    public String getReps() {
        return this.repetitions + "reps";
    }

    public String getReposLong() {
        return getHourMinTime(this.reposLong);
    }

    public String getSeances() {
        String str = "séance";
        if (this.seances > 1) {
            str += "s";
        }
        return this.seances + str;
    }


    ////////////////////////////////////////////////////////////////


    ////////-- setteur pour la date de modification/utilisation --//////
    public void modificationDate() {
        this.lastModified = new Date(System.currentTimeMillis());
    }
    /////////////////////////////////////////////////////////////////////

    /////-- setteur pour la sauvegarde --///////////////
    public void saveProgression(Integer numeroRepetition, Integer numeroSeance) {
        this.state.add(numeroRepetition);
        this.state.add(numeroSeance);
        modificationDate();
    }
}
