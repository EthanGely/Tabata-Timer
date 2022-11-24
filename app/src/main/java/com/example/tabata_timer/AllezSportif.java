package com.example.tabata_timer;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.database.dbSettings.Settings;
import com.example.tabata_timer.utility.Compteur;
import com.example.tabata_timer.utility.OnFinishListenner;
import com.example.tabata_timer.utility.OnUpdateListener;

public class AllezSportif extends AppCompatActivity implements OnUpdateListener, OnFinishListenner {

    private DatabaseClient mDb;

    public static final String EXERCICE_KEY = "exercice_key";
    // DATA
    private Compteur compteur;

    protected TextView timer;
    private Exercice exo;
    private boolean isFirstExo = true;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allez_sportif);

        // Initialiser l'objet Compteur
        compteur = new Compteur();

        // Abonner l'activité au compteur pour "suivre" les événements
        compteur.addOnUpdateListener(this);
        compteur.addOnFinishListenner(this);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        //Récupération de l'id de l'exercice lancé
        int id = getIntent().getIntExtra(EXERCICE_KEY, -1);
        //Si l'id n'est pas trouvé (=-1) on quitte l'activité.
        if (id == -1) {
            finish();
            return;
        }
        //Sinon, on récupère l'objet Exercice associé
        getExercice(id);

    }

    /**
     * Récupère l'objet Exercice grâce à un ID.
     *
     * @param id
     */
    private void getExercice(int id) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetExercice extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                Exercice exo = mDb.getAppDatabase().exerciceDao().findExerciceByID(id);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                if (exercice == null) {
                    finish();
                    return;
                }
                //Récupération de l'exercice
                exo = exercice;
                //Affichage des info de cet exo
                exo.modificationDate();
                getSettings();
            }
        }
        GetExercice gt = new GetExercice();
        gt.execute();
    }

    /**
     * Affiche les infos présentes dans l'attribut 'exo'
     */
    private void afficherInfosExercice() {

        if (isFirstExo && (exo.getNumeroRepetition() != 1 || exo.getNumeroSeance() != 1 || !exo.getIsSport() || exo.getIsRepos() || exo.getIsReposLong())) {
            popUpContinue();
        }

        //Nom de l'exercice
        TextView nomExo = findViewById(R.id.nomExo);
        nomExo.setText(String.valueOf(exo.getNomExercice()));

        //Type d'activité en cours (effort, repos, repos long)
        TextView typeAction = findViewById(R.id.typeAction);
        typeAction.setText(String.valueOf(exo.getTypeAction()));

        //Mise à jour de la liste des prochaines activitées
        updateListeActivites();

        int durationMillis = exo.getMiliSec(exo.getTempsEnCours());

        //TIMER
        timer = findViewById(R.id.timer);
        //Récupération du temps de l'activité en cours
        compteur.setTimer(durationMillis);

        ProgressBar pBar = findViewById(R.id.progress);
        pBar.setMax(durationMillis);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pBar.setMin(0);
        }

        if (exo.getIsSport()) {
            pBar.setProgress(durationMillis);
        } else {
            pBar.setProgress(0);
        }


        // Mise à jour graphique
        miseAJour();
        //Si l'activité actuelle n'est pas la première, le chrono se relance automatiquement
        //Sinon, on attends le clic user.
        if (!isFirstExo) {
            compteur.start();
        }
    }


    /**
     * Affiche un popUp si l'exercice n'a pas été terminé précédemment
     * Si l'utilisateur shouaite recommencer, l'exercice est réinitialisé, sinon il ne se passe rien.
     */
    private void popUpContinue() {
        new AlertDialog.Builder(AllezSportif.this).setTitle("Reprendre l'exercice ?").setMessage("Vous n'avez pas fini cet exercice la dernère fois. Voulez-vous le reprendre là où vous vous étiez arrêté ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton( "non, reprendre du début", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        exo.resetExo();

                        saveExercice();
                    }
                }).setIcon(android.R.drawable.ic_menu_rotate).show();
    }

    /**
     * Récupère les activités qui suivent l'activité actuelle
     */
    private void updateListeActivites() {

        //Récupération de l'exercice en cours (le dernier effectué)
        TextView typeExo = findViewById(R.id.typeAction);
        String lastTypeExo = (String) typeExo.getText();

        //Si le dernier exercice est une préparation, on ne modifie rien (un Exercice n'a pas conaissance de la préparation)

            //puis on récupère les numéros actuels de rep/séance,
            int numRepetition = exo.getNumeroRepetition();
            int numSeance = exo.getNumeroSeance();

            //le nombre total de reps/séances
            int nbSeances = exo.getSeances();
            int nbReps = exo.getRepetitions();

            //sans oublier les durées de chaque activité
            /*String tempsSport = exo.getHourMinTime(exo.getSport());
            String tempsRepos = exo.getHourMinTime(exo.getRepos());
            String tempsReposLong = exo.getHourMinTime(exo.getReposLong());

             */


            addExerciceToList();

            //Tant qu'il reste des séances et des exercices à faire
            /*
            while (numSeance <= nbSeances && numRepetition <= nbReps) {
                if (lastTypeExo.contains("Effort")) {
                    if (numRepetition == nbReps) {
                        if (numSeance == nbSeances) {
                            lastTypeExo = "FIN DE L'EXERCICE";
                            addExerciceToList(lastTypeExo, linearLayout, numSeance, numRepetition);
                        } else {
                            lastTypeExo = "Repos long : " + tempsReposLong;
                            addExerciceToList(lastTypeExo, linearLayout, numSeance, numRepetition);
                        }
                        numSeance++;
                        numRepetition = 1;
                    } else {
                        lastTypeExo = "Repos : " + tempsRepos;
                        addExerciceToList(lastTypeExo, linearLayout, numSeance, numRepetition);
                    }
                } else if (lastTypeExo.contains("Repos") && !lastTypeExo.contains("long")) {
                    numRepetition++;
                    lastTypeExo = "Effort : " + tempsSport;
                    addExerciceToList(lastTypeExo, linearLayout, numSeance, numRepetition);
                } else {
                    //numSeance += 1;
                    lastTypeExo = "Effort : " + tempsSport;
                    addExerciceToList(lastTypeExo, linearLayout, numSeance, numRepetition);
                }
            }
            
             */
    }

    /*public void addExerciceToList(String text, LinearLayout linearLayout, int numSeance, int numRepetition) {
        if (text.contains("Effort")) {
            TextView textView2 = new TextView(this);
            textView2.setText("seance=" + numSeance + " repetiton=" + numRepetition);
            linearLayout.addView(textView2);
        }

        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        linearLayout.addView(textView);
    }
    
     */

    /**
     * Récupère la liste des actions suivantes et les ajoute dans le layout.
     */
    public void addExerciceToList() {
        //Récupération et "vidage" du layout
        LinearLayout linearLayout = findViewById(R.id.linearListeActions);
        linearLayout.removeAllViews();

        //Récupération du type de la première activité (Effort, Repos, Repos Long)
        TextView typeActivite = findViewById(R.id.typeAction);
        //Récupération des activités qui suivent la première activité
        String[][][] listeActivites = exo.getFollowingActivities(String.valueOf(typeActivite.getText()));

        // -1 car l'index du 1er élément est 0
        int iValue = exo.getNumeroSeance() - 1;

        //Pour chaque Séance (Nombre Y de groupe de X séries de Effort - Repos)
        for (int i = iValue; i < listeActivites.length; i++) {
            //On récupère le nombre x de répétitions (-1 pour l'index du 1er élément)
            int jValue = exo.getNumeroRepetition() - 1;
            //Pour les Séances != la 1ere, on remet j à 0;
            if (i != iValue) {
                jValue = 0;
            }
            //Pour chaque répétition :
            for (int j = jValue; j < listeActivites[i].length; j++) {
                TextView textView;
                //Si la répétition n'est pas vide :
                if (listeActivites[i][j][0] != null || listeActivites[i][j][1] != null) {
                    //On affiche le numéro de la séance et de la répétition
                    textView = new TextView(this);
                    textView.setText("seance=" + (i + 1) + " repetiton=" + (j + 1));
                    linearLayout.addView(textView);

                    //Puis, pour chaque élément de la répétition :
                    for (int k = 0; k < listeActivites[i][j].length; k++) {
                        //Si l'élément n'est pas vide :
                        if (listeActivites[i][j][k] != null) {
                            //On affiche la valeur contenue dans l'array
                            textView = new TextView(this);
                            textView.setText(listeActivites[i][j][k]);
                            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
                            linearLayout.addView(textView);
                        }
                    }
                }
            }
        }
    }


    /**
     * Met en pause ou relance le timer.
     *
     * @param view
     */
    public void onPauseResumeTimer(View view) {
        Button btn = findViewById(R.id.pauseResume);
        if (!compteur.getIsStarted()) {
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            btn.setText("Pause");
            onStartTimer();
        } else {
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            btn.setText("Démarrer");
            onPauseTimer();
        }
    }


    // Lancer le compteur
    private void onStartTimer() {
        if (exo.getIsSport()) {
            MediaPlayer song = MediaPlayer.create(AllezSportif.this, R.raw.sifflet);
            song = setVolume(song);
            song.start();
        }
        compteur.start();
        //pBar.getProgressDrawable().setColorFilter(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private MediaPlayer setVolume(MediaPlayer song) {
        if (!settings.getIsSoundOn()) {
            song.setVolume(0, 0);
        }
        return song;
    }

    // Mettre en pause le compteur
    private void onPauseTimer() {
        compteur.pause();
    }


    // Mise à jour graphique
    private void miseAJour() {
        // Affichage des informations du compteur
        timer.setText("" + compteur.getMinutes() + ":" + String.format("%02d", compteur.getSecondes()) + ":" + String.format("%03d", compteur.getMillisecondes()));
        ProgressBar pBar = findViewById(R.id.progress);

        if (exo.getIsSport()) {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - (exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis()));
        } else {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis());
        }

        if ((exo.getIsRepos() || exo.getIsReposLong()) && compteur.getSecondes() <= 2) {
            TextView t = findViewById(R.id.typeAction);
            t.setText("Préparez-vous !");
        }
    }

    /**
     * Méthode appelée à chaque update du compteur (l'activité est abonnée au compteur)
     */
    public void onUpdate() {
        miseAJour();
    }

    public void onFinish() {
        isFirstExo = false;
        compteur.pause();

        if (exo.tempsFini()) {
            endExercice(true);
        } else {
            afficherInfosExercice();
        }
    }

    public void onGiveUp(View view) {
        new AlertDialog.Builder(AllezSportif.this).setTitle("Abandon ?").setMessage("Voulez-vous vraiment abandonner cet exercice ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "LOOSER", Toast.LENGTH_SHORT).show();
                        endExercice(false);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }


    private void saveExercice() {
        exo.modificationDate();
        class UpdateExo extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                mDb.getAppDatabase().exerciceDao().update(exo);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                getExercice((int) exo.getId());
            }
        }
        UpdateExo gt = new UpdateExo();
        gt.execute();
    }
    private void endExercice(boolean show) {
        exo.modificationDate();
        class UpdateExo extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                mDb.getAppDatabase().exerciceDao().update(exo);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                alertFinExercice(show);
            }
        }
        UpdateExo gt = new UpdateExo();
        gt.execute();
    }

    private void alertFinExercice(boolean show) {
        if (show) {
            new AlertDialog.Builder(AllezSportif.this).setTitle("Exercice fini !").setMessage("Bravo, vous avez fini cet exercice !\nVous avez obtenu une étoile, c'est la " + exo.getNbEtoilesF() + " sur cet exerice !")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Retour à la liste", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_OK);
                            finish();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Refaire cet exercice", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(RESULT_FIRST_USER);
                            finish();
                        }
                    }).setIcon(android.R.drawable.btn_star_big_on).show();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void getSettings() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetSettings extends AsyncTask<Void, Void, Settings> {

            @Override
            protected Settings doInBackground(Void... voids) {
                Settings stg = mDb.getAppDatabase().settingsDao().getSettings();
                return stg;
            }

            @Override
            protected void onPostExecute(Settings stg) {
                super.onPostExecute(stg);
                settings = stg;
                afficherInfosExercice();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSettings gt = new GetSettings();
        gt.execute();
    }

}