package com.example.tabata_timer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.Exercice;
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
    private boolean isPrepared = false;

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

        int id = (int) getIntent().getIntExtra(EXERCICE_KEY, -1);
        if (id == -1) {
            finish();
        }

        getExercice(id);

    }

    private void getExercice(int id) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetExercice extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                Exercice exo = mDb.getAppDatabase()
                        .exerciceDao()
                        .findExerciceByID(id);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                exo = exercice;
                afficherInfosExercice();
            }
        }
        GetExercice gt = new GetExercice();
        gt.execute();
    }

    private void afficherInfosExercice() {
        TextView nomExo = findViewById(R.id.nomExo);
        nomExo.setText(String.valueOf(exo.getNomExercice()));

        TextView typeAction = findViewById(R.id.typeAction);
        typeAction.setText(String.valueOf(exo.getTypeAction()));

        updateListeActivites();

        //TIMER
        timer = findViewById(R.id.timer);

        compteur.setTimer(exo.getMiliSec(exo.getTempsEnCours()));

        // Mise à jour graphique

        miseAJour();
        if (!isFirstExo) {
            compteur.start();
        }
    }

    private void updateListeActivites() {

        TextView typeExo = findViewById(R.id.typeAction);
        String lastTypeExo = (String) typeExo.getText();
        if (!lastTypeExo.contains("Préparez-vous !")) {

            LinearLayout linearLayout = findViewById(R.id.linearListeActions);
            linearLayout.removeAllViews();
            int numRepetition = exo.getNumeroRepetition();
            int numSeance = exo.getNumeroSeance();

            int nbSeances = exo.getSeances();
            int nbReps = exo.getRepetitions();

            String tempsSport = exo.getSport() + " " + exo.getTypeOfTime(exo.getSport());
            String tempsRepos = exo.getRepos() + " " + exo.getTypeOfTime(exo.getRepos());
            String tempsReposLong = exo.getReposLong() + " " + exo.getTypeOfTime(exo.getReposLong());

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
                        //numRepetition++;
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
        }
    }

    public void addExerciceToList(String text, LinearLayout linearLayout, int numSeance, int numRepetition) {
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

    public void onPauseResumeTimer(View view) {
        if (!compteur.getIsStarted()) {
            onStartTimer();
        } else {
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
        timer.setText("" + compteur.getMinutes() + ":"
                + String.format("%02d", compteur.getSecondes()) + ":"
                + String.format("%03d", compteur.getMillisecondes()));

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

        //Temps de préparation après un repos / repos long
        if ((exo.getIsRepos() || exo.getIsReposLong()) && !isPrepared) {
            compteur.setTimer(3000);
            TextView typeAction = findViewById(R.id.typeAction);
            typeAction.setText(String.valueOf("Préparez-vous !"));
            compteur.start();
            isPrepared = true;
            return;
        }

        if (exo.tempsFini()) {
            finish();
        } else {
            isPrepared = false;
            afficherInfosExercice();
        }
    }

    public void onGiveUp(View view) {
        new AlertDialog.Builder(AllezSportif.this)
                .setTitle("Abandon ?")
                .setMessage("Voulez-vous vraiment abandonner cet exercice ?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "LOOSER", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}