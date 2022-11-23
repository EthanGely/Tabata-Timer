package com.example.tabata_timer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Database;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.Exercice;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tabata_Timer extends AppCompatActivity {

    private ArrayList<Exercice> listeExercices = new ArrayList<>();

    private int id;
    private final int requestCreate = 0;
    private final int requestUpdate = 1;
    private final int requestStart = 2;
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
        TextView date = linearTmp.findViewById(R.id.dateExo);

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
        date.setText((String) exercice.getLastModified().toString());

        // Ajouter un événement au bouton de modification

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int) exercice.getId();
                Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
                startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

                // Lancement de la demande de changement d'activité
                startActivityForResult(startExoIntent, requestStart);
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
                startActivityForResult(createExoIntent, requestUpdate);
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
        startActivityForResult(createExoIntent, requestCreate);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == requestCreate || requestCode == requestUpdate || requestCode == requestStart) && resultCode == RESULT_OK) {
            // Mise à jour des taches
            getExercices();
        }else if (requestCode == requestStart && resultCode == RESULT_FIRST_USER) {
            Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
            startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

            // Lancement de la demande de changement d'activité
            startActivityForResult(startExoIntent, requestStart);
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
                sortListeExercice();
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



    // Array A[] has the items to sort; array B[] is a work array.
    private ArrayList<Exercice> TopDownMergeSort(ArrayList<Exercice> A)
    {
        int n = A.size();
        ArrayList<Exercice> B = new ArrayList<>(A);
        CopyArray(A, 0, n, B);
        return TopDownSplitMerge(B, 0, n, A);   // sort data from B[] into A[]
    }

    // Split A[] into 2 runs, sort both runs into B[], merge both runs from B[] to A[]
// iBegin is inclusive; iEnd is exclusive (A[iEnd] is not in the set).
    private ArrayList<Exercice> TopDownSplitMerge(ArrayList<Exercice> B, int iBegin, int iEnd, ArrayList<Exercice> A)
    {
        if (iEnd - iBegin <= 1)                     // if run size == 1
            return B;                                 //   consider it sorted
        // split the run longer than 1 item into halves
        int iMiddle = (iEnd + iBegin) / 2;              // iMiddle = mid point
        // recursively sort both runs from array A[] into B[]
        TopDownSplitMerge(A, iBegin,  iMiddle, B);  // sort the left  run
        TopDownSplitMerge(A, iMiddle,    iEnd, B);  // sort the right run
        // merge the resulting runs from array B[] into A[]
        return TopDownMerge(B, iBegin, iMiddle, iEnd, A);
    }

    //  Left source half is A[ iBegin:iMiddle-1].
// Right source half is A[iMiddle:iEnd-1   ].
// Result is            B[ iBegin:iEnd-1   ].
    private ArrayList<Exercice> TopDownMerge(ArrayList<Exercice> A, int iBegin, int iMiddle, int iEnd, ArrayList<Exercice> B)
    {
        int i = iBegin;
        int j = iMiddle;

        // While there are elements in the left or right runs...
        for (int k = iBegin; k < iEnd; k++) {
            // If left run head exists and is <= existing right run head.
            if (i < iMiddle && (j >= iEnd || A.get(i).getLastModified().compareTo(A.get(j).getLastModified()) > 0)) {
                B.set(k, A.get(i));
                i = i + 1;
            } else {
                B.set(k, A.get(j));
                j = j + 1;
            }
        }
        return B;
    }

    void CopyArray(ArrayList<Exercice> A, int iBegin, int iEnd,  ArrayList<Exercice> B)
    {
        for (int k = iBegin; k < iEnd; k++)
            B.set(k, A.get(k));
    }



    private void sortListeExercice() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listeExercices.sort(Collections.reverseOrder(Comparator.comparing(Exercice::getLastModified)));
        }else{
            listeExercices = new ArrayList<>(TopDownMergeSort(listeExercices));
        }
    }

    public void onResumeLastWorkout(View view) {
        int id = (int) listeExercices.get(0).getId();
        Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
        startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

        // Lancement de la demande de changement d'activité
        startActivityForResult(startExoIntent, requestStart);
    }
}