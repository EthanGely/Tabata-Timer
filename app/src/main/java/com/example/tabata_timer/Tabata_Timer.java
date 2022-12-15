package com.example.tabata_timer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.database.dbSettings.Settings;
import com.example.tabata_timer.utility.MyApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tabata_Timer extends AppCompatActivity {

    private final int requestCreate = 0;
    private final int requestUpdate = 1;
    private final int requestStart = 2;
    private ArrayList<Exercice> listeExercices = new ArrayList<>();
    private int id;
    // DATA
    private DatabaseClient mDb;

    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata_timer);

        ((MyApplication) this.getApplication()).stopCompteur();
        ((MyApplication) this.getApplication()).stopSound();

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        //Récupération des settings.
        //Cette fonction se chargera aussi de récupérer les exercices, des les trier, d'ajouter les favoris...
        getSettings();
    }

    private void addExercices() {
        LinearLayout linear = findViewById(R.id.linearListeExos);
        linear.removeAllViews();
        LinearLayout fils;

        for (int i = 0; i < listeExercices.size(); i++) {
            if (i % 2 == 0) {
                //Création d'un nouveau linearLayout
                int idCard = View.generateViewId();
                fils = new LinearLayout(this);
                fils.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                fils.setOrientation(LinearLayout.HORIZONTAL);
                fils.setId(idCard);
                linear.addView(fils);
            }

            LinearLayout linearTmp = createNewExercice(listeExercices.get(i));
            linear.addView(linearTmp);
        }
    }

    @SuppressWarnings("deprecation")
    public LinearLayout createNewExercice(Exercice exercice) {
        //Récupération du template complet
        @SuppressLint("InflateParams") LinearLayout linearTmp = (LinearLayout) getLayoutInflater().inflate(R.layout.template_exercice, null);

        //Récupération des textView du template
        TextView typeExo = linearTmp.findViewById(R.id.Exo_type);
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
        typeExo.setText(exercice.getTypeExercice().toString());
        nomExo.setText(exercice.getNomExercice());
        sportExo.setText(exercice.getSportF());
        reposExo.setText(exercice.getReposF());
        repsExo.setText(exercice.getRepetitionsF());
        reposLongExo.setText(exercice.getReposLongF());
        seancesExo.setText(exercice.getSeancesF());


        // Ajouter un événement au bouton de modification

        btnStart.setOnClickListener(view -> {
            id = (int) exercice.getId();
            Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
            startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, id);

            // Lancement de la demande de changement d'activité
            startActivityForResult(startExoIntent, requestStart);
        });


        // Ajouter un événement au bouton de modification
        modifier.setOnClickListener(view -> {
            int idExo = (int) exercice.getId();
            Intent createExoIntent = new Intent(Tabata_Timer.this, CreateExercice.class);
            createExoIntent.putExtra(CreateExercice.MODIFIER_KEY, true);
            createExoIntent.putExtra(CreateExercice.EXERCICE_KEY, idExo);

            // Lancement de la demande de changement d'activité
            startActivityForResult(createExoIntent, requestUpdate);
        });

        imgStar.setOnClickListener(view -> setFavori((int) exercice.getId(), exercice.getNomExercice()));

        return linearTmp;
    }

    private void setFavori(int idFav, String nomExo) {
        if (settings.getIdFavori() == idFav) {
            settings.setIdFavori(-1);
            Toast.makeText(getApplicationContext(), nomExo + " supprimé des favoris", Toast.LENGTH_LONG).show();
        } else {
            settings.setIdFavori(idFav);
            Toast.makeText(getApplicationContext(), nomExo + " ajouté aux favoris", Toast.LENGTH_LONG).show();
        }

        updateSettings(settings);
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
                                        imageStar.setBackground(AppCompatResources.getDrawable(this, android.R.drawable.star_big_on));
                                    } else {
                                        imageStar.setBackground(AppCompatResources.getDrawable(this, android.R.drawable.star_big_off));
                                    }
                                } else {
                                    imageStar.setBackground(AppCompatResources.getDrawable(this, android.R.drawable.star_big_off));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void onCreateWorkout(View view) {
        Intent createExoIntent = new Intent(Tabata_Timer.this, CreateExercice.class);
        createExoIntent.putExtra(CreateExercice.MODIFIER_KEY, false);
        // Lancement de la demande de changement d'activité
        startActivityForResult(createExoIntent, requestCreate);
    }


    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((MyApplication) this.getApplication()).stopCompteur();
        ((MyApplication) this.getApplication()).stopSound();

        if ((requestCode == requestCreate || requestCode == requestUpdate || requestCode == requestStart) && resultCode == RESULT_OK) {
            // Mise à jour des taches
            getExercices();
        } else if (requestCode == requestStart && resultCode == RESULT_FIRST_USER) {
            Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
            startExoIntent.putExtra(AllezSportif.EXERCICE_KEY, id);

            // Lancement de la demande de changement d'activité
            startActivityForResult(startExoIntent, requestStart);
        }
    }


    private void activerDesactiverBoutonReprendre() {
        Button btnReprendre = findViewById(R.id.resumeWorkout);
        LinearLayout linearLastWokout = findViewById(R.id.linearLastWorkout);
        if (listeExercices.isEmpty()) {
            btnReprendre.setClickable(false);
            btnReprendre.setVisibility(View.GONE);
        } else {
            btnReprendre.setClickable(true);
            linearLastWokout.setBackground(AppCompatResources.getDrawable(this, R.drawable.blue_gradient));
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
                posExo--;
            }
        }
    }

    public Exercice findUsingIterator(int id, ArrayList<Exercice> exercices) {
        for (Exercice exo : exercices) {
            if (exo.getId() == id) {
                return exo;
            }
        }
        return null;
    }

    /**
     * Continue / recommence le dernier exercice lancé
     *
     * @param view vue
     */
    @SuppressWarnings("deprecation")
    public void onResumeLastWorkout(View view) {
        if (!listeExercices.isEmpty()) {
            //Récupération de l'ID du premier exercice de la liste triée
            //Il est aussi possible d'utiliser l'attribut de classe "id" qui est mis à jour au lancement d'un exercice.
            int idExo = (int) listeExercices.get(0).getId();
            Intent startExoIntent = new Intent(Tabata_Timer.this, AllezSportif.class);
            startExoIntent.putExtra(CreateExercice.EXERCICE_KEY, idExo);

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
            btnSon.setBackground(AppCompatResources.getDrawable(this, R.drawable.sound_on));
        } else {
            btnSon.setBackground(AppCompatResources.getDrawable(this, R.drawable.sound_off));
        }
    }

    private void getExercices() {
        mDb = DatabaseClient.getInstance(getApplicationContext());
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class GetExercices extends AsyncTask<Void, Void, List<Exercice>> {

            @Override
            protected List<Exercice> doInBackground(Void... voids) {
                return mDb.getAppDatabase().exerciceDao().getAll();
            }

            @Override
            protected void onPostExecute(List<Exercice> exercices) {
                super.onPostExecute(exercices);
                listeExercices.clear();
                listeExercices.addAll(exercices);
                sortListeExercice();
                activerDesactiverBoutonReprendre();
                addExercices();
                updateStars();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetExercices gt = new GetExercices();
        gt.execute();
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
                    setSettings(new Settings());
                    return;
                }
                settings = stg;

                setLogoVolume();
                getExercices();
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
        @SuppressLint("StaticFieldLeak")
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