package com.example.tabata_timer.database.dbExercices;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.tabata_timer.AllezSportif;
import com.example.tabata_timer.R;
import com.example.tabata_timer.utility.DateConverter;
import com.example.tabata_timer.utility.TypeExercice;

import java.util.Date;

@Entity(tableName = "exercice")
@TypeConverters(DateConverter.class)
public class Exercice {

    ////////////////////Attributs/////////////////////////
    //Nom de l'exercice
    @PrimaryKey(autoGenerate = true)
    private long id;

    //Le nom de l'exercice
    private String nomExercice;

    //Durées en secondes
    private int sport, repos, reposLong;

    //Nombre total de séances / répétitions
    private int repetitions, seances;

    //Numéro actuel de la répétition / séance (commence à 1)
    private int numeroRepetition = 1, numeroSeance = 1;

    //Quelle est l'activité en cours (de base, le sport)
    private boolean isSport = true, isRepos = false, isReposLong = false;

    //Date de dernière modification/lancement
    private Date lastModified;

    private int nbEtoiles = 0;

    private TypeExercice typeExercice;

    private int currentTime;


    ///////////////////////////////////////////////////////////////////////


    /////////////////////////-- Constructeurs --///////////////////////////
    public Exercice() {
    }

    public Exercice(String nomExercice, int tempsDeSport, int tempsDeRepos, int nombreDeRepetitions, int reposLong, int nombreDeSeances, TypeExercice typeExercice) {
        //On ajoute le nom de l'exercice et les valeurs associées - date de modification mise à jour
        modifierExercice(nomExercice, tempsDeSport, tempsDeRepos, nombreDeRepetitions, reposLong, nombreDeSeances, typeExercice);
    }
    ///////////////////////////////////////////////////////////////////////

    ////////////////////////////GETTEURS / SETTEURS ///////////////////////
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNomExercice() {
        return nomExercice;
    }

    public void setNomExercice(String s) {
        nomExercice = s;
    }

    public int getSport() {
        return this.sport;
    }

    public void setSport(int s) {
        sport = s;
    }

    public int getRepos() {
        return this.repos;
    }

    public void setRepos(int r) {
        repos = r;
    }

    public int getReposLong() {
        return this.reposLong;
    }

    public void setReposLong(int rl) {
        reposLong = rl;
    }

    public int getRepetitions() {
        return this.repetitions;
    }

    public void setRepetitions(int rep) {
        repetitions = rep;
    }

    public int getSeances() {
        return this.seances;
    }

    public void setSeances(int se) {
        seances = se;
    }

    public int getNumeroRepetition() {
        return numeroRepetition;
    }

    public void setNumeroRepetition(int nr) {
        numeroRepetition = nr;
    }

    public int getNumeroSeance() {
        return numeroSeance;
    }

    public void setNumeroSeance(int ns) {
        numeroSeance = ns;
    }

    public boolean getIsSport() {
        return isSport;
    }

    public void setIsSport(boolean b) {
        isSport = b;
    }

    public boolean getIsRepos() {
        return isRepos;
    }

    public void setIsRepos(boolean b) {
        isRepos = b;
    }

    public boolean getIsReposLong() {
        return isReposLong;
    }

    public void setIsReposLong(boolean b) {
        isReposLong = b;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date d) {
        this.lastModified = d;
    }

    public int getNbEtoiles() {
        return nbEtoiles;
    }

    public void setNbEtoiles(int nb) {
        nbEtoiles = nb;
    }

    public TypeExercice getTypeExercice() {
        return this.typeExercice;
    }

    public void setTypeExercice(TypeExercice tpE) {
        this.typeExercice = tpE;
    }

    public int getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    ////////////////////////////////////////////////////////////////////


    /**
     * Si la durée donnée (en secondes) est un nombre d'heures entières, ou un nombre de minutes entières,
     * la fonction renverra "heures" ou "minutes". Sinon, "secondes" sera renvoyé.
     * <p>
     * Exemple : getTypeOfTime(3600) = "heures"; getTypeOfTime(3660) = "minutes"; //Car 3660 = 1h01.
     *
     * @param secondes entier représentant les secondes
     * @return String
     */
    public String getTypeOfTime(int secondes) {
        int hours = secondes / 3600;
        int minutes = secondes / 60;
        if (secondes % 3600 == 0 && hours > 0) {
            return checkNumber(hours, "heure");
        } else if (secondes % 60 == 0 && minutes > 0) {
            return checkNumber(minutes, "minute");
        } else {
            return checkNumber(secondes, "seconde");
        }
    }


    /**
     * De la même manière que getTypeOfTime, la fonction retourne cette fois-ci le nombre d'heures ou de minutes entières dans secondes.
     * Si le nombre d'heures et/ou de minutes ne sont pas des entiers, retourne le nombre de secondes.
     *
     * @param secondes entier représentant les secondes
     * @return int
     */
    public int getBestTime(int secondes) {
        double hours = secondes / (double) 3600;
        double minutes = secondes / (double) 60;
        if (secondes % 3600 == 0 && hours > 0) {
            return (int) hours;
        } else if (secondes % 60 == 0 && minutes > 0) {
            return (int) minutes;
        } else {
            return secondes;
        }
    }


    /**
     * Retourne une chaine xx heures yy minutes zz secondes
     * getHourMinTime(3725) => "1 heure 2 minutes 5 secondes"
     *
     * @param secondes entier représentant les secondes
     * @return String
     */
    public String getHourMinTime(int secondes) {
        //Nombre entier d'heure(s)
        int hours = secondes / 3600;
        //Nombre entier de minutes - les heures
        int minutes = (secondes - (hours * 3600)) / 60;
        //Nombre entier de secondes - (heures + minutes)
        int remainingSecs = ((secondes - ((hours * 3600) + minutes * 60)));

        String temps = "";

        if (hours > 0) {
            temps = hours + checkNumber(hours, " heure");
        } else if (minutes > 0) {
            temps += " " + minutes + checkNumber(minutes, " minute");
        } else {
            temps += " " + remainingSecs + checkNumber(remainingSecs, " seconde");
        }
        return temps;
    }


    /**
     * Retourne la durée de l'activité en cours
     *
     * @return int
     */
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

    /**
     * Appelée à la fin d'une activité (par ex à la fin des x secondes de sport, avant le repos).
     * Modifie le numéro de répétition / séance si besoin, les boléens de l'activité en cours.
     * Renvoie "true" si l'exercice est entièrement terminé, false sinon.
     *
     * @return boolean
     */
    public boolean tempsFini() {
        if (isSport) {
            isSport = false;
            if (numeroRepetition == repetitions) {
                if (numeroSeance == seances) {
                    setNbEtoiles(nbEtoiles + 1);
                    modificationDate();
                    resetExo();
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
        }
        return false;
    }

    /**
     * Permet de modifier directement toutes les variables "modifiables" (sans avoir à passer par les getteurs setteurs directement).
     * Met à jour automatiquement la date modification.
     *
     * @param nomExercice         nom de l'exercice
     * @param tempsDeSport        durée en secondes d'effort
     * @param tempsDeRepos        durée en secondes de repos (court)
     * @param nombreDeRepetitions nombre entier de répétitions
     * @param reposLong           durée en secondes de repos long
     * @param nombreDeSeances     nombre entier de séances
     */
    public void modifierExercice(String nomExercice, int tempsDeSport, int tempsDeRepos, int nombreDeRepetitions, int reposLong, int nombreDeSeances, TypeExercice typeExo) {
        setNomExercice(nomExercice);
        setSport(tempsDeSport);
        setRepos(tempsDeRepos);
        setRepetitions(nombreDeRepetitions);
        setReposLong(reposLong);
        setSeances(nombreDeSeances);
        setTypeExercice(typeExo);

        modificationDate();
    }

    /**
     * Retourne la durée en milisecondes.
     *
     * @param secondes durée entière en secondes
     * @return milisecondes
     */
    public int getMiliSec(int secondes) {
        return secondes * 1000;
    }


    /**
     * Si l'entier donné est supérieur à 1, ajoute un "s" à la chiane donnée.
     *
     * @param number nombre entier
     * @param string chaine à mettre ou non au pluriel
     * @return chaine au pluriel si besoin
     */
    public String checkNumber(int number, String string) {
        if (number > 1) {
            string += "s";
        }
        return string;
    }


    /**
     * Retourne une chaine correctement formatée pour l'affichage de la durée de sport d'un exercice
     * Exemple : "1 heure 2 minutes 5 secondes d'effort"
     *
     * @return durée_sport_formatée
     */
    public String getSportF() {
        return getHourMinTime(this.sport) + " d'effort";
    }

    /**
     * Retourne une chaine correctement formatée pour l'affichage de la durée de repos d'un exercice
     * Exemple : "1 heure 2 minutes 5 secondes de repos"
     *
     * @return durée_repos_formatée
     */
    public String getReposF() {
        return getHourMinTime(this.repos) + " de repos";
    }

    /**
     * Retourne une chaine correctement formatée pour l'affichage du nombre de répétitions d'un exercice
     * Exemple : "5 répétitions"
     *
     * @return nombre_répétitions formatté
     */
    public String getRepetitionsF() {
        return this.repetitions + checkNumber(this.repetitions, " répétition");
    }

    /**
     * Retourne une chaine correctement formatée pour l'affichage de la durée de repos long d'un exercice
     * Exemple : "1 heure 2 minutes 5 secondes de repos long"
     *
     * @return durée_repos_long_formatée
     */
    public String getReposLongF() {
        return getHourMinTime(this.reposLong) + " de repos long";
    }

    /**
     * Retourne une chaine correctement formatée pour l'affichage du nombre de séances d'un exercice
     * Exemple : "3 séances"
     *
     * @return nombre_seance_formaté
     */
    public String getSeancesF() {
        return this.seances + checkNumber(this.seances, " séance");
    }

    /**
     * Met à jour la date de dernière modification avec l'heure actuelle
     */
    public void modificationDate() {
        this.lastModified = new Date(System.currentTimeMillis());
    }

    /**
     * Retourne une chaine de caractères correspondant à l'activité en cours.
     *
     * @return Nom_activité
     */
    public String getTypeAction() {
        if (isSport) {
            return "Effort";
        } else if (isRepos) {
            return "Repos";
        } else {
            return "Repos long";
        }
    }


    /**
     * Retourne la liste des activités suivates
     *
     * @return String[][][] liste des activités
     */
    public String[][][] getFollowingActivities(String lastActivity) {
        int numSeance = this.numeroSeance;
        int nbSeances = this.seances;

        int numRepetition = this.numeroRepetition;
        int nbReps = this.repetitions;


        String[][][] listeActivites = new String[nbSeances][nbReps][2];

        while (numSeance <= nbSeances && numRepetition <= nbReps) {
            if (lastActivity.contains("Effort")) {
                if (numRepetition == nbReps) {
                    if (numSeance == nbSeances) {
                        listeActivites[numSeance - 1][numRepetition - 1][1] = "FIN DE L'EXERCICE";
                        break;
                    } else {
                        listeActivites[numSeance - 1][numRepetition - 1][1] = "Repos long : " + getHourMinTime(reposLong);
                    }
                    lastActivity = "Repos long";
                    numSeance++;
                    numRepetition = 1;
                } else {
                    listeActivites[numSeance - 1][numRepetition - 1][1] = "Repos : " + getHourMinTime(repos);
                    lastActivity = "Repos";
                }
            } else {
                if (lastActivity.contains("Repos") && !lastActivity.contains("long")) {
                    numRepetition++;
                }
                listeActivites[numSeance - 1][numRepetition - 1][0] = "Effort : " + getHourMinTime(sport);
                lastActivity = "Effort";
            }
        }
        return listeActivites;
    }

    public String getNbEtoilesF() {
        String suffixe = "ème";
        if (nbEtoiles == 1) {
            suffixe = "ère";
        }
        return nbEtoiles + suffixe;
    }

    public void resetExo() {
        numeroRepetition = 1;
        numeroSeance = 1;
        isSport = true;
        isRepos = false;
        isReposLong = false;
    }
}
