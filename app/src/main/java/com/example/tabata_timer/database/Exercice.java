package com.example.tabata_timer.database;

import java.util.ArrayList;
import java.util.Date;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.tabata_timer.utility.DateConverter;

@Entity(tableName = "exercice")
@TypeConverters(DateConverter.class)
public class Exercice {

    ////////////////////Attributs//////////////
    //Nom de l'exercice
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nomExercice;

    private int sport, repos, repetitions, reposLong, seances, numeroRepetition, numeroSeance, lastTypeExo;
    private boolean isSport, isRepos, isReposLong;


    //Date de dernière modification/lancement
    private Date lastModified;

    public int getTempsEnCours() {
        if (isSport) {
            return sport;
        } else if (isRepos) {
            return repos;
        } else if (isReposLong) {
            return reposLong;
        }
        return -1;
    }

    public boolean tempsFini() {
        if (isSport) {
            isSport = false;
            if (numeroRepetition == repetitions) {
                if (numeroSeance == seances) {
                    return true;
                }
                numeroSeance += 1;
                numeroRepetition = 1;
                isReposLong = true;
            } else {
                isRepos = true;
            }
        } else if (isRepos) {
            isRepos = false;
            isSport = true;
            numeroRepetition += 1;
        } else if (isReposLong) {
            isReposLong = false;
            isSport = true;
            //numeroSeance += 1;
        }
        return false;
    }

    public String getNextTypeAction(int numRepet, int numSeance, String lastTypeExo) {
        if (lastTypeExo.contains("Effort")) {
            if (numRepet == repetitions) {
                if (numSeance >= seances) {
                    return "FIN DE LEXERCICE";
                }
                return "Repos long : " + reposLong + " " + getTypeOfTime(reposLong);
            } else {
                return "Repos : " + repos + " " + getTypeOfTime(repos);
            }
        } else {
            return "Effort : " + sport + " " + getTypeOfTime(sport);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLastTypeExo() {
        return lastTypeExo;
    }

    public void setLastTypeExo(int typeExo) {
        this.lastTypeExo = typeExo;
    }


    //////////////////GETTEURS / SETTEURS /////////////////
    public void setNomExercice(String s) {
        nomExercice = s;
    }

    public void setIsSport(boolean b) {
        isSport = b;
    }

    public void setIsRepos(boolean b) {
        isRepos = b;
    }

    public void setIsReposLong(boolean b) {
        isReposLong = b;
    }

    public void setSport(int s) {
        sport = s;
    }

    public void setRepos(int r) {
        repos = r;
    }

    public void setRepetitions(int rep) {
        repetitions = rep;
    }

    public void setReposLong(int rl) {
        reposLong = rl;
    }

    public void setSeances(int se) {
        seances = se;
    }

    public void setNumeroRepetition(int nr) {
        numeroRepetition = nr;
    }

    public void setNumeroSeance(int ns) {
        numeroSeance = ns;
    }

    public void setLastModified(Date d) {
        this.lastModified = d;
    }


    public int getSport() {
        return this.sport;
    }

    public int getRepos() {
        return this.repos;
    }

    public int getRepetitions() {
        return this.repetitions;
    }

    public int getReposLong() {
        return this.reposLong;
    }

    public int getSeances() {
        return this.seances;
    }

    public int getNumeroRepetition() {
        return numeroRepetition;
    }

    public int getNumeroSeance() {
        return numeroSeance;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean getIsSport() {
        return isSport;
    }

    public boolean getIsRepos() {
        return isRepos;
    }

    public boolean getIsReposLong() {
        return isReposLong;
    }

    public String getTypeOfTime(int secondes) {
        double hours = secondes / 3600;
        double minutes = secondes / 60;
        if (hours % 1 == 0 && hours > 0) {
            return "heures";
        } else if (minutes % 1 == 0 && minutes > 0) {
            return "minutes";
        } else {
            return "secondes";
        }
    }
    ///////////////////////////////////////////////////////


    ////////////////-- Constructeur --//////////////
    public Exercice() {}

    public Exercice(String nomExercice, int tempsDeSport, int tempsDeRepos, int nombreDeRepetitions, int reposLong, int nombreDeSeances) {

        //On ajoute le nom de l'exercice et les valeurs associées
        modifierExercice(nomExercice, tempsDeSport, tempsDeRepos, nombreDeRepetitions, reposLong, nombreDeSeances);

        this.lastTypeExo = 1;
        isSport = true;
        isRepos = false;
        isReposLong = false;


        //L'exercice n'a jamais été lancé (modification automatique de la date)
        saveProgression(1, 1);

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


    public int getBestTime(int secondes) {
        double hours = secondes / 3600;
        double minutes = secondes / 60;
        if (hours % 1 == 0 && hours > 0) {
            return (int) hours;
        } else if (minutes % 1 == 0 && minutes > 0) {
            return (int) minutes;
        } else {
            return secondes;
        }
    }

    public int getMiliSec(int secondes) {
        return secondes * 1000;
    }

    //Retourne un temps en heures minutes secondes (si les heures ou les minutes sont vides, ces champs n'aparaissent pas
    private String getHourMinTime(int secondes) {
        int hours = (int) secondes / 3600;
        int remaining = (int) secondes - hours * 3600;
        int mins = remaining / 60;
        remaining = remaining - mins * 60;
        String temps = "";

        if (hours > 0) {
            temps = checkNumber(hours, " heure");
            ;

        }
        if (mins > 0) {
            temps += " " + checkNumber(mins, " minute");
            ;
        }

        if (remaining > 0) {
            temps += " " + checkNumber(remaining, " seconde");
        }
        return temps;
    }


    //Conccatène le nombre et la chaine donné; Ajoute un "s" à la fin si le nombre est > 1.
    public String checkNumber(int nbr, String str) {
        if (nbr > 1) {
            str += "s";
        }
        return nbr + str;
    }

    //////////-- getteur pour les valeurs d'exercice --/////////////

    public String getSportF() {
        return getHourMinTime(this.sport) + " d'exercice";
    }

    public String getReposF() {
        return getHourMinTime(this.repos) + " de repos";
    }

    public String getRepetitionsF() {
        return checkNumber(this.seances, " répétition");
    }

    public String getReposLongF() {
        return getHourMinTime(this.reposLong) + " de repos long";
    }

    public String getSeancesF() {
        return checkNumber(this.seances, " séance");
    }


    ////////////////////////////////////////////////////////////////


    ////////-- setteur pour la date de modification/utilisation --//////
    public void modificationDate() {
        this.lastModified = new Date(System.currentTimeMillis());
    }
    /////////////////////////////////////////////////////////////////////

    /////-- setteur pour la sauvegarde --///////////////
    public void saveProgression(Integer numeroRepetition, Integer numeroSeance) {
        this.numeroRepetition = numeroRepetition;
        this.numeroSeance = numeroSeance;
        modificationDate();
    }

    public String getTypeAction() {
        if (isSport) {
            return "Effort";
        } else if (isRepos) {
            return "Repos";
        } else {
            return "Repos long";
        }
    }
}
