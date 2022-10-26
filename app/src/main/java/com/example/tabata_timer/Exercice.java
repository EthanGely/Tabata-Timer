package com.example.tabata_timer;

import java.util.Date;
import java.util.Map;

public class Exercice {

    ////////////////////Attributs//////////////
    //Nom de l'exercice
    private String nomExercice;

    //Données sur l'exercice (variables ci-dessus)
    private Map<String, Integer> sportExercice;

    //Date de dernière modification/lancement
    private Date lastModified;

    //Données sur l'état (variables ci-dessus)
    private Map<String, Integer> state;


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
        this.sportExercice.put("temps de sport", tempsDeSport);
        this.sportExercice.put("temps de repos", tempsDeRepos);
        this.sportExercice.put("nombre de repetitions", nombreDeRepetitions);
        this.sportExercice.put("repos long", reposLong);
        this.sportExercice.put("nombre de seances", nombreDeSeances);
        modificationDate();
    }
    /////////////////////////////////////////////////////////////////


    //////////-- getteur pour les valeurs d'exercice --/////////////
    public Map<String, Integer> getSportExercice() {
        return this.sportExercice;
    }
    ////////////////////////////////////////////////////////////////


    ////////-- setteur pour la date de modification/utilisation --//////
    public void modificationDate() {
        this.lastModified = new Date(System.currentTimeMillis());
    }
    /////////////////////////////////////////////////////////////////////

    /////-- setteur pour la sauvegarde --///////////////
    public void saveProgression(int numeroRepetition, int numeroSeance) {
        this.state.put("numero repetition", 0);
        this.state.put("numero seance", 0);
        modificationDate();
    }
}
