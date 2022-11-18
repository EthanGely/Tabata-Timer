package com.example.tabata_timer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.Exercice;

import java.util.ArrayList;
import java.util.List;

public class Tabata_Timer extends AppCompatActivity {

    private ArrayList<Exercice> listeExercices = new ArrayList<>();

    private int id;

    // DATA
    private DatabaseClient mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata_timer);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        getExercices();
    }

    private void addExercices() {
        LinearLayout linear = findViewById(R.id.linearListeExos);
        linear.removeAllViews();
        LinearLayout fils = null;

        for (int i = 0; i < listeExercices.size(); i++) {


            if (i % 2 == 0) {
                //Création d'un nouveau linearLayout
                id = View.generateViewId();
                fils = new LinearLayout(this);
                fils.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                fils.setOrientation(LinearLayout.HORIZONTAL);
                fils.setId(id);
                linear.addView(fils);
            }else{
                fils = findViewById(id);
            }


            LinearLayout linearTmp = createNewExercice(listeExercices.get(i), linear, i);

            //Structure :
            // <(Constraint Layout)>
            //      <Linear Layout (linearListeExos) - vertical>
            //          <Linear Layout - Horizontal>            //A créer une fois sur 2
            //              - Template Exercice                 //Ajouter au nouveau layout
            //              - Template Exercice                 //Ajouter au nouveau layout
            //          </Linear Layout>
            //      </Linear Layout>
            // </(Constraint Layout)>


            linear.addView(linearTmp);

        }
    }

    public LinearLayout createNewExercice(Exercice exercice, LinearLayout linear, int i) {
        //Récupération du template complet
        LinearLayout linearTmp = (LinearLayout) getLayoutInflater().inflate(R.layout.template_exercice, null);

        //Récupération du linear layout du template
        LinearLayout layoutExo = linearTmp.findViewById(R.id.Exo);

        //Récupération des textView du template
        TextView nomExo = linearTmp.findViewById(R.id.Exo_nom);
        TextView sportExo = linearTmp.findViewById(R.id.Exo_sport);
        TextView reposExo = linearTmp.findViewById(R.id.Exo_repos);
        TextView repsExo = linearTmp.findViewById(R.id.Exo_reps);
        TextView reposLongExo = linearTmp.findViewById(R.id.Exo_repos_long);
        TextView seancesExo = linearTmp.findViewById(R.id.Exo_seances);

        ImageButton modifier = linearTmp.findViewById(R.id.modifierExo);
        ImageButton supprimer = linearTmp.findViewById(R.id.supprimerExo);

        Button btnStart = linearTmp.findViewById(R.id.startExercice);


        //Définition du texte pour chaque textView
        nomExo.setText(exercice.getNomExercice());
        sportExo.setText(exercice.getSportF());
        reposExo.setText(exercice.getReposF());
        repsExo.setText(exercice.getRepetitionsF());
        reposLongExo.setText(exercice.getReposLongF());
        seancesExo.setText(exercice.getSeancesF());

        // Ajouter un événement au bouton de modification

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) exercice.getId();
                Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
                startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

                // Lancement de la demande de changement d'activité
                startActivity(startExoIntent);
            }
        });




        // Ajouter un événement au bouton de modification
        modifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) exercice.getId();
                Intent createExoIntent = new Intent(Tabata_Timer.this, CreateExercice.class);
                createExoIntent.putExtra(CreateExercice.MODIFIER_KEY, true);
                createExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

                // Lancement de la demande de changement d'activité
                startActivityForResult(createExoIntent, 0);
            }
        });



        // Ajouter un événement au bouton de suppression
        supprimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(Tabata_Timer.this)
                        .setTitle("Supprimer " + exercice.getNomExercice())
                        .setMessage("Voulez-vous vraiment supprimer l'exercice " + exercice.getNomExercice())

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                supprimerExercice(exercice);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return linearTmp;
    }

    public static void setMargins (View v, int l, int r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, 0, r, 0);
            v.requestLayout();
        }
    }

    public void onCreateWorkout(View view) {
        Intent createExoIntent = new Intent(Tabata_Timer.this, CreateExercice.class);
        createExoIntent.putExtra(CreateExercice.MODIFIER_KEY, false);
        // Lancement de la demande de changement d'activité
        startActivityForResult(createExoIntent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK) {

            // Mise à jour des taches
            getExercices();
        }
    }



    private void getExercices() {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetExercices extends AsyncTask<Void, Void, List<Exercice>> {

            @Override
            protected List<Exercice> doInBackground(Void... voids) {
                List<Exercice> exoList = mDb.getAppDatabase()
                        .exerciceDao()
                        .getAll();
                return exoList;
            }

            @Override
            protected void onPostExecute(List<Exercice> exercices) {
                super.onPostExecute(exercices);
                listeExercices.clear();
                listeExercices.addAll(exercices);
                addExercices();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetExercices gt = new GetExercices();
        gt.execute();
    }

    private void supprimerExercice(Exercice exo) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class SupprimerExercices extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                mDb.getAppDatabase()
                        .exerciceDao()
                        .delete(exo);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                getExercices();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        SupprimerExercices gt = new SupprimerExercices();
        gt.execute();
    }
}