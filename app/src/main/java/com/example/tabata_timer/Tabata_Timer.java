package com.example.tabata_timer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.database.dbSettings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Tabata_Timer extends AppCompatActivity {

    private ArrayList<Exercice> listeExercices = new ArrayList<>();

    private int id;
    private final int requestCreate = 0;
    private final int requestUpdate = 1;
    private final int requestStart = 2;
    // DATA
    private DatabaseClient mDb;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata_timer);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        getSettings();

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
            } else {
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

        ImageButton modifier = linearTmp.findViewById(R.id.modifierExo);

        Button btnStart = linearTmp.findViewById(R.id.startExercice);

        ImageView imgStar = linearTmp.findViewById(R.id.imgStar);


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

        imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFavori((int) exercice.getId(), exercice.getNomExercice());
            }
        });

        return linearTmp;
    }

    private void setFavori(int id, String nomExo) {
        if (settings.getIdFavori() == id) {
            settings.setIdFavori(-1);
            Toast.makeText(getApplicationContext(), nomExo + " supprimé des favoris", Toast.LENGTH_LONG).show();

        } else {
            settings.setIdFavori(id);
            Toast.makeText(getApplicationContext(), nomExo + " ajouté aux favoris", Toast.LENGTH_LONG).show();
        }
        updateSettings(settings);

        sortListeExercice();
        addExercices();
        updateStars();
    }

    private void updateStars() {
        LinearLayout allExo = findViewById(R.id.linearListeExos);

        int count = allExo.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = allExo.getChildAt(i);

            if (v instanceof LinearLayout) {
                View v2 = ((LinearLayout) v).getChildAt(0);
                if (v2 instanceof LinearLayout) {
                    View v3 = ((LinearLayout) v).getChildAt(0);
                    if (v3 instanceof LinearLayout) {
                        View v4 = ((LinearLayout) v3).getChildAt(0);
                        if (v4 instanceof LinearLayout) {
                            View imageStar = ((LinearLayout) v4).getChildAt(2);
                            View title = ((LinearLayout) v4).getChildAt(1);
                            if (imageStar instanceof ImageView && title instanceof TextView) {
                                int index = listeExercices.indexOf(findUsingIterator(settings.getIdFavori(), listeExercices));
                                if (index != -1) {
                                    String nomExo = listeExercices.get(index).getNomExercice();
                                    if (((TextView) title).getText() == nomExo) {
                                        imageStar.setBackground(getDrawable(android.R.drawable.star_big_on));
                                    } else {
                                        imageStar.setBackground(getDrawable(android.R.drawable.star_big_off));
                                    }
                                } else {
                                    imageStar.setBackground(getDrawable(android.R.drawable.star_big_off));
                                }
                            }
                        }
                    }
                }
            }
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
        } else if (requestCode == requestStart && resultCode == RESULT_FIRST_USER) {
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
                List<Exercice> exoList = mDb.getAppDatabase().exerciceDao().getAll();
                return exoList;
            }

            @Override
            protected void onPostExecute(List<Exercice> exercices) {
                super.onPostExecute(exercices);
                listeExercices.clear();
                listeExercices.addAll(exercices);
                activerDesactiverBoutonReprendre();
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

    private void activerDesactiverBoutonReprendre() {
        Button btnReprendre = findViewById(R.id.resumeWorkout);
        LinearLayout linearLastWokout = findViewById(R.id.linearLastWorkout);
        if (listeExercices.isEmpty()) {
            btnReprendre.setClickable(false);
            btnReprendre.setVisibility(View.GONE);
        } else {
            btnReprendre.setClickable(true);
            linearLastWokout.setBackground(getResources().getDrawable(R.drawable.blue_gradient));
            btnReprendre.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.transparent)));
        }
    }


    /**
     * Fonction qui trie les Exercices données via leur date de modification, de la plus récente à la plus ancienne.
     * Groupe de fonctions (TopDownMergeSort, TopDownSplitMerge, TopDownMerge)  issues de Wikipedia https://en.wikipedia.org/wiki/Merge_sort
     *
     * @param A ArrayList[Exercice]
     * @return ArrayList[Exercice]
     */
    // Array A[] has the items to sort; array B[] is a work array.
    private ArrayList<Exercice> TopDownMergeSort(ArrayList<Exercice> A) {
        int n = A.size();
        ArrayList<Exercice> B = new ArrayList<>(A);
        return TopDownSplitMerge(B, 0, n, A);   // sort data from B[] into A[]
    }

    // Split A[] into 2 runs, sort both runs into B[], merge both runs from B[] to A[]
    // iBegin is inclusive; iEnd is exclusive (A[iEnd] is not in the set).
    private ArrayList<Exercice> TopDownSplitMerge(ArrayList<Exercice> B, int iBegin, int iEnd, ArrayList<Exercice> A) {
        if (iEnd - iBegin <= 1)                     // if run size == 1
            return B;                                 //   consider it sorted
        // split the run longer than 1 item into halves
        int iMiddle = (iEnd + iBegin) / 2;              // iMiddle = mid point
        // recursively sort both runs from array A[] into B[]
        TopDownSplitMerge(A, iBegin, iMiddle, B);  // sort the left  run
        TopDownSplitMerge(A, iMiddle, iEnd, B);  // sort the right run
        // merge the resulting runs from array B[] into A[]
        return TopDownMerge(B, iBegin, iMiddle, iEnd, A);
    }

    //  Left source half is A[ iBegin:iMiddle-1].
    // Right source half is A[iMiddle:iEnd-1   ].
    // Result is            B[ iBegin:iEnd-1   ].
    private ArrayList<Exercice> TopDownMerge(ArrayList<Exercice> A, int iBegin, int iMiddle, int iEnd, ArrayList<Exercice> B) {
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


    /**
     * trie la liste des exercices, l'exercice le plus "récent" en premier.
     */
    private void sortListeExercice() {
        //Si possible, trie les exercices via leur date de modification, de la plus récente à la plus vielle
        //Avec un comparateur.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listeExercices.sort(Collections.reverseOrder(Comparator.comparing(Exercice::getLastModified)));
        } else {
            //Sinon, on fait appel à une série de fonctions issues de Wikipedia
            listeExercices = new ArrayList<>(TopDownMergeSort(listeExercices));
        }

        if (settings.getIdFavori() != -1) {
            permutFavori(settings.getIdFavori());
        }
    }

    private void permutFavori(int id) {

        int posExo = listeExercices.indexOf(findUsingIterator(id, listeExercices));

        if (posExo > 0) {
            for (int i = posExo - 1; i >= 0; i--) {
                Collections.rotate(listeExercices.subList(i, posExo + 1), -1);
                posExo --;
            }
        }
    }

    public Exercice findUsingIterator(int id, ArrayList<Exercice> exercices) {
        Iterator<Exercice> iterator = exercices.iterator();
        while (iterator.hasNext()) {
            Exercice exo = iterator.next();
            if (exo.getId() == id) {
                return exo;
            }
        }
        return null;
    }

    /**
     * Continue / recommence le dernier exercice lancé
     *
     * @param view
     */
    public void onResumeLastWorkout(View view) {
        if (!listeExercices.isEmpty()) {
            //Récupération de l'ID du premier exercice de la liste triée
            int id = (int) listeExercices.get(0).getId();
            Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
            startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

            // Lancement de la demande de changement d'activité
            startActivityForResult(startExoIntent, requestStart);
        }

    }

    public void onChangeVolume(View view) {
        //On inverse la valeur du son
        settings.setSoundOn(!settings.getIsSoundOn());
        setLogoVolume();
        updateSettings(settings);
    }

    private void setLogoVolume() {
        ImageButton btnSon = findViewById(R.id.btnSon);
        if (settings.getIsSoundOn()) {
            btnSon.setBackground(getDrawable(R.drawable.sound_on));
        } else {
            btnSon.setBackground(getDrawable(R.drawable.sound_off));
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
                if (stg == null) {
                    setSettings(new Settings());
                }
                settings = stg;
                setLogoVolume();

            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSettings gt = new GetSettings();
        gt.execute();
    }

    private void setSettings(Settings settings) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class GetSettings extends AsyncTask<Void, Void, Settings> {

            @Override
            protected Settings doInBackground(Void... voids) {
                mDb.getAppDatabase().settingsDao().insert(settings);
                return settings;
            }

            @Override
            protected void onPostExecute(Settings settings) {
                super.onPostExecute(settings);
                getSettings();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetSettings gt = new GetSettings();
        gt.execute();
    }

    private void updateSettings(Settings settings) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        class UpdateSettings extends AsyncTask<Void, Void, Settings> {

            @Override
            protected Settings doInBackground(Void... voids) {
                mDb.getAppDatabase().settingsDao().update(settings);
                return settings;
            }

            @Override
            protected void onPostExecute(Settings settings) {
                super.onPostExecute(settings);
                getSettings();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        UpdateSettings gt = new UpdateSettings();
        gt.execute();
    }
}