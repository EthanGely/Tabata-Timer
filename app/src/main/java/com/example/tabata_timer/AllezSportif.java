package com.example.tabata_timer;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.database.dbSettings.Settings;
import com.example.tabata_timer.utility.Compteur;
import com.example.tabata_timer.utility.MyApplication;
import com.example.tabata_timer.utility.OnFinishListenner;
import com.example.tabata_timer.utility.OnUpdateListener;

import java.util.Locale;

public class AllezSportif extends AppCompatActivity implements OnUpdateListener, OnFinishListenner {

    //id de l'exercice séléctionné
    public static final String EXERCICE_KEY = "exercice_key";

    //
    protected TextView timer;
    private DatabaseClient mDb;
    private Compteur compteur;
    private Exercice exo;
    private boolean isFirstExo = true;
    private boolean isRestartPlayed = false;
    private boolean isFistSoundPlayed = false;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allez_sportif);

        // Initialiser l'objet Compteur
        compteur = new Compteur();

        ((MyApplication) this.getApplication()).setCompteur(compteur);

        // Abonner l'activité au compteur pour "suivre" les événements
        compteur.addOnUpdateListener(this);
        compteur.addOnFinishListenner(this);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        //Récupération de l'id de l'exercice lancé
        int id = getIntent().getIntExtra(EXERCICE_KEY, -1);
        //Si l'id n'est pas trouvé (=-1) on quitte l'activité.
        if (id == -1) {
            Toast.makeText(getApplicationContext(), "Erreur dans la récupération de l'exercice", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        //Sinon, on récupère l'objet Exercice associé
        getExercice(id);

    }

    @Override
    public void onStop(){
        super.onStop();
        changeBackground("sportToPause");
        onPauseTimer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        afficherInfosExercice();
        compteur.setTimer(exo.getCurrentTime());
        String text = compteur.getMinutes() + ":" + String.format(Locale.FRANCE, "%02d", compteur.getSecondes()) + ":" + String.format(Locale.FRANCE, "%03d", compteur.getMillisecondes());
        timer.setText(text);
        ProgressBar pBar = findViewById(R.id.progress);

        if (exo.getIsSport()) {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - (exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis()));
        } else {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis());
        }
    }

    /**
     * Récupère l'objet Exercice grâce à un ID.
     *
     * @param id id de l'exercice
     */
    private void getExercice(int id) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class GetExercice extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                return mDb.getAppDatabase().exerciceDao().findExerciceByID(id);
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                if (exercice == null) {
                    Toast.makeText(getApplicationContext(), "Erreur dans la récupération de l'exercice", Toast.LENGTH_LONG).show();
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

        isRestartPlayed = false;

        ImageView logoAction = findViewById(R.id.logoAction);
        switch (exo.getTypeExercice()) {
            case REPOS:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.dodo));
                break;
            case POMPES:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.pompe));
                break;
            case SQUATS:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.squat));
                break;
            case TRACTIONS:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.tractions));
                break;
            case JUMPIN_JACKS:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.jumping_jack));
                break;
            case MULTISPORT:
                logoAction.setBackground(AppCompatResources.getDrawable(this, R.drawable.multisport));
                break;
        }


        //Nom de l'exercice
        TextView nomExo = findViewById(R.id.nomExo);
        nomExo.setText(String.valueOf(exo.getNomExercice()));

        //Type d'activité en cours (effort, repos, repos long)
        TextView typeAction = findViewById(R.id.typeAction);
        typeAction.setText(String.valueOf(exo.getTypeAction()));

        TextView numSeance = findViewById(R.id.numSeance);
        String textSeance = "Séance " + exo.getNumeroSeance() + "/" + exo.getSeances();
        numSeance.setText(textSeance);

        TextView numRep = findViewById(R.id.numRep);
        String textRep = "Répétition " + exo.getNumeroRepetition() + "/" + exo.getRepetitions();
        numRep.setText(textRep);
        //Mise à jour de la liste des prochaines activitées
        updateListeActivites();

        int durationMillis = exo.getMiliSec(exo.getTempsEnCours());

        //TIMER
        timer = findViewById(R.id.timer);
        //Récupération du temps de l'activité en cours
        compteur.setTimer(durationMillis);

        ProgressBar pBar = findViewById(R.id.progress);
        //pBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN);
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

            MediaPlayer song = new MediaPlayer();
            ((MyApplication) this.getApplication()).setSoundEffect(song, settings.getVol());
            if (exo.getIsSport()) {
                changeBackground("sleepToSport");
                if (exo.getNumeroRepetition() == exo.getRepetitions() && exo.getNumeroSeance() == exo.getSeances()) {
                    ((MyApplication) this.getApplication()).playAlmostThere();
                }
            } else {
                changeBackground("sportToSleep");
                ((MyApplication) this.getApplication()).playSifflet();
            }

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
                .setNegativeButton("non, reprendre du début", (dialog, which) -> {
                    exo.resetExo();

                    saveExercice();
                }).setIcon(android.R.drawable.ic_menu_rotate).show();
    }

    /**
     * Récupère la liste des actions suivantes et les ajoute dans le layout.
     */
    private void updateListeActivites() {
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
                    String text = "Séance " + (i + 1) + "  -  Répétiton " + (j + 1);
                    textView.setText(text);
                    textView.setBackground(AppCompatResources.getDrawable(this, R.drawable.gray_gradient));
                    textView.setPadding(20, 20, 20, 20);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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


    private void changeBackground(String col) {
        LinearLayout main = findViewById(R.id.main);
        if (col.equals("begin")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.black2green));
        } else if (col.equals("sportToSleep")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.green2red));
        } else if (col.equals("sleepToSport")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.red2green));
        } else if (col.equals("sportToPause")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.green2orange));
        } else if (col.equals("pauseToSport")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.orange2green));
        } else if (col.equals("sleepToPause")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.red2orange));
        } else if (col.equals("pauseToSleep")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.orange2red));
        } else if (col.equals("sportToEnd")) {
            main.setBackground(AppCompatResources.getDrawable(this, R.drawable.green2end));
        }

        TransitionDrawable transition = (TransitionDrawable) main.getBackground();
        transition.startTransition(500);
    }


    /**
     * Met en pause ou relance le timer.
     *
     * @param view vue
     */
    public void onPauseResumeTimer(View view) {
        Button btn = findViewById(R.id.pauseResume);
        if (!compteur.getIsStarted()) {
            if (isFirstExo && !isFistSoundPlayed) {
                MediaPlayer song = new MediaPlayer();
                ((MyApplication) this.getApplication()).setSoundEffect(song, settings.getVol());
                ((MyApplication) this.getApplication()).playLetsGo();
                changeBackground("begin");
            } else {
                if (exo.getIsSport()) {
                    changeBackground("pauseToSport");
                } else {
                    changeBackground("pauseToSleep");
                }

            }
            isFistSoundPlayed = true;
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
            btn.setText(R.string.pause);
            onStartTimer();
        } else {
            if (exo.getIsSport()) {
                changeBackground("sportToPause");
            } else {
                changeBackground("sleepToPause");
            }
            btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
            btn.setText(R.string.demarrer);
            onPauseTimer();
        }
    }


    // Lancer le compteur
    private void onStartTimer() {
        compteur.start();
    }

    // Mettre en pause le compteur
    private void onPauseTimer() {
        compteur.pause();
    }


    // Mise à jour graphique
    private void miseAJour() {
        // Affichage des informations du compteur
        exo.setCurrentTime(compteur.getRemainingMilis());
        String text = compteur.getMinutes() + ":" + String.format(Locale.FRANCE, "%02d", compteur.getSecondes()) + ":" + String.format(Locale.FRANCE, "%03d", compteur.getMillisecondes());
        timer.setText(text);
        ProgressBar pBar = findViewById(R.id.progress);

        if (exo.getIsSport()) {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - (exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis()));
        } else {
            pBar.setProgress(exo.getMiliSec(exo.getTempsEnCours()) - compteur.getRemainingMilis());
        }

        if ((exo.getIsRepos() || exo.getIsReposLong()) && compteur.getSecondes() <= 2) {
            TextView t = findViewById(R.id.typeAction);
            t.setText(R.string.prep);
        }

        if ((exo.getIsRepos() || exo.getIsReposLong()) && compteur.getSecondes() == 3 && compteur.getMinutes() == 0 && !isRestartPlayed) {
            MediaPlayer song = new MediaPlayer();
            ((MyApplication) this.getApplication()).setSoundEffect(song, settings.getVol());
            ((MyApplication) this.getApplication()).playRestart();
            isRestartPlayed = true;
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
            if (settings.getIsSoundOn()) {
                ((MyApplication) this.getApplication()).playEnd();
            }
            changeBackground("sportToEnd");
            endExercice(true);
        } else {
            afficherInfosExercice();
        }
    }

    public void onGiveUp(View view) {
        boolean wasStarted = compteur.getIsStarted();
        if (wasStarted) {
            onPauseTimer();
        }
        new AlertDialog.Builder(AllezSportif.this).setTitle("Abandon ?").setMessage("Voulez-vous vraiment abandonner cet exercice ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    Toast.makeText(getApplicationContext(), "LOOSER", Toast.LENGTH_SHORT).show();
                    endExercice(false);
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    if (wasStarted) {
                        onStartTimer();
                    }
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }


    private void saveExercice() {
        exo.modificationDate();
        @SuppressLint("StaticFieldLeak")
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
        @SuppressLint("StaticFieldLeak")
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
            new AlertDialog.Builder(AllezSportif.this).setTitle("Exercice fini !").setMessage("Bravo, vous avez fini cet exercice !\n\nC'est la " + exo.getNbEtoilesF() + " fois que vous terminez cet exerice !")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("Retour à la liste", (dialog, which) -> {
                        setResult(RESULT_OK);
                        finish();
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Refaire cet exercice", (dialog, which) -> {
                        setResult(RESULT_FIRST_USER);
                        finish();
                    }).setIcon(android.R.drawable.btn_star_big_on).show();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    private void getSettings() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class GetSettings extends AsyncTask<Void, Void, Settings> {

            @Override
            protected Settings doInBackground(Void... voids) {
                return mDb.getAppDatabase().settingsDao().getSettings();
            }

            @Override
            protected void onPostExecute(Settings stg) {
                super.onPostExecute(stg);
                if (stg == null) {
                    Toast.makeText(getApplicationContext(), "Erreur dans la récupération des paramètres", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED);
                    finish();
                    return;
                }
                settings = stg;
                setLogoVolume();
                afficherInfosExercice();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSettings gt = new GetSettings();
        gt.execute();
    }

    public void onChangeVolume(View view) {
        //On inverse la valeur du son
        settings.setSoundOn(!settings.getIsSoundOn());
        ((MyApplication) this.getApplication()).setVolume(settings.getVol());
        setLogoVolume();
        updateSettings(settings);
    }

    private void setLogoVolume() {
        ImageButton btnSon = findViewById(R.id.btnSon);
        if (settings.getIsSoundOn()) {
            btnSon.setBackground(AppCompatResources.getDrawable(this, R.drawable.sound_on));
        } else {
            btnSon.setBackground(AppCompatResources.getDrawable(this, R.drawable.sound_off));
            ((MyApplication) this.getApplication()).stopSound();
        }
    }

    private void updateSettings(Settings settings) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class UpdateSettings extends AsyncTask<Void, Void, Settings> {

            @Override
            protected Settings doInBackground(Void... voids) {
                mDb.getAppDatabase().settingsDao().update(settings);
                return settings;
            }

            @Override
            protected void onPostExecute(Settings settings) {
                super.onPostExecute(settings);
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        UpdateSettings gt = new UpdateSettings();
        gt.execute();
    }

}