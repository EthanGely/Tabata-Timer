package com.example.tabata_timer;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.dbExercices.Exercice;
import com.example.tabata_timer.utility.TypeExercice;

public class CreateExercice extends AppCompatActivity {

    public static final String MODIFIER_KEY = "modifier_key";
    public static final String EXERCICE_KEY = "exercice_key";
    // DATA
    private DatabaseClient mDb;
    private Exercice exerciceSave;

    private boolean nameChecked = false;

    private int id = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercice);

        boolean isModifier = (boolean) getIntent().getSerializableExtra(MODIFIER_KEY);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        final Spinner spinnerTypeExo = findViewById(R.id.choixTypeExo);
        final Spinner spinnerTempsEffort = findViewById(R.id.chxEffort);
        final Spinner spinnerTempsRepos = findViewById(R.id.chxRepos);
        final Spinner spinnerTempsReposLong = findViewById(R.id.chxReposLong);

        Button btnSuppr = findViewById(R.id.supprimerExo);

        String[] typesTemps = {"secondes", "minutes", "heures"};
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typesTemps);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinnerTempsEffort.setAdapter(dataAdapterR);
        spinnerTempsRepos.setAdapter(dataAdapterR);
        spinnerTempsReposLong.setAdapter(dataAdapterR);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We're in day time, or we don't know when we are !
                spinnerTypeExo.setAdapter(new ArrayAdapter<>(this, R.layout.better_spinner_day, TypeExercice.values()));
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                spinnerTypeExo.setAdapter(new ArrayAdapter<>(this, R.layout.better_spinner_night, TypeExercice.values()));
                break;
        }

        if (isModifier) {
            id = getIntent().getIntExtra(EXERCICE_KEY, -1);
            if (id < 0) {
                finish();
            }
            //Récupère et affiche les données de l'exercice
            btnSuppr.setVisibility(View.VISIBLE);
            getExercice(id);
        }
    }


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
                exerciceSave = exercice;
                addExercice();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        GetExercice gt = new GetExercice();
        gt.execute();
    }


    public void setSpinnerValue(Spinner sp, int secondes, TypeExercice typeExo) {
        int spinnerPosition;
        if (secondes > -1 && typeExo == null) {
            ArrayAdapter<String> myAdap = (ArrayAdapter<String>) sp.getAdapter();
            spinnerPosition = myAdap.getPosition(exerciceSave.getTypeOfTime(secondes));
        } else {
            ArrayAdapter<TypeExercice> myAdap = (ArrayAdapter<TypeExercice>) sp.getAdapter();
            spinnerPosition = myAdap.getPosition(typeExo);
        }

        sp.setSelection(spinnerPosition);
    }

    private void addExercice() {
        EditText nomExo = findViewById(R.id.nomExo);
        nomExo.setText(exerciceSave.getNomExercice());

        setSpinnerValue(findViewById(R.id.choixTypeExo), -1, exerciceSave.getTypeExercice());


        EditText tempsSport = findViewById(R.id.tempsSport);
        tempsSport.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getSport())));
        setSpinnerValue(findViewById(R.id.chxEffort), exerciceSave.getSport(), null);


        EditText tempsRepos = findViewById(R.id.tempsRepos);
        tempsRepos.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getRepos())));

        setSpinnerValue(findViewById(R.id.chxRepos), exerciceSave.getRepos(), null);


        EditText reps = findViewById(R.id.reps);
        reps.setText(String.valueOf(exerciceSave.getRepetitions()));


        EditText reposLong = findViewById(R.id.reposLong);
        reposLong.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getReposLong())));
        setSpinnerValue(findViewById(R.id.chxReposLong), exerciceSave.getReposLong(), null);


        EditText seances = findViewById(R.id.seances);
        seances.setText(String.valueOf(exerciceSave.getSeances()));

        Button btnValider = findViewById(R.id.createWorkout);
        btnValider.setText(R.string.modifier);
    }

    public void onCancel(View view) {
        finish();
    }

    public void checkName(String nomExo) {

        char[] chars = nomExo.toCharArray();
        int nbr;
        if (nomExo.matches("[A-Z]*[a-z]*[0-9]*\\([0-9]+\\)")) {
            nbr = (Integer.parseInt(String.valueOf(chars[chars.length - 2]))) + 1;
            nomExo = nomExo.split("\\(")[0];
        } else {
            nbr = 1;
        }
        getExerciceByName(nomExo + "(" + nbr + ")", id);

    }


    private void getExerciceByName(String nomExo, int idExo) {
        @SuppressLint("StaticFieldLeak")
        class GetExerciceByName extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {

                // adding to database
                return mDb.getAppDatabase().exerciceDao().findExerciceByName(nomExo, idExo);
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                if (exercice == null) {
                    nameChecked = true;
                    createWorkout(nomExo);
                } else {
                    checkName(exercice.getNomExercice());
                }
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        GetExerciceByName st = new GetExerciceByName();
        st.execute();
    }

    public void onCreateWorkout(View view) {
        //Tous les editText
        final EditText nomExo = findViewById(R.id.nomExo);
        final String nomExerciceString = String.valueOf(nomExo.getText());

        final EditText tmpsSport = findViewById(R.id.tempsSport);
        final EditText tmpsRepos = findViewById(R.id.tempsRepos);
        final EditText nbReps = findViewById(R.id.reps);
        final EditText tmpsReposL = findViewById(R.id.reposLong);
        final EditText nbSeance = findViewById(R.id.seances);


        if (nomExerciceString.isEmpty() || String.valueOf(tmpsSport.getText()).isEmpty() || String.valueOf(tmpsRepos.getText()).isEmpty() || String.valueOf(nbReps.getText()).isEmpty() || String.valueOf(tmpsReposL.getText()).isEmpty() || String.valueOf(nbSeance.getText()).isEmpty()) {
            //On vérifie que les champs ne soient pas vide (ils sont TOUS required)
            if (String.valueOf(nbSeance.getText()).isEmpty()) {
                nbSeance.setError("Valeur requise");
                nbSeance.requestFocus();
            }

            if (String.valueOf(tmpsReposL.getText()).isEmpty()) {
                tmpsReposL.setError("Valeur requise");
                tmpsReposL.requestFocus();
            }

            if (String.valueOf(nbReps.getText()).isEmpty()) {
                nbReps.setError("Valeur requise");
                nbReps.requestFocus();
            }

            if (String.valueOf(tmpsRepos.getText()).isEmpty()) {
                tmpsRepos.setError("Valeur requise");
                tmpsRepos.requestFocus();
            }

            if (String.valueOf(tmpsSport.getText()).isEmpty()) {
                tmpsSport.setError("Valeur requise");
                tmpsSport.requestFocus();
            }

            if (nomExerciceString.isEmpty()) {
                nomExo.setError("Nom requis");
                nomExo.requestFocus();
            }
        } else if (String.valueOf(tmpsSport.getText()).contains(".") || String.valueOf(tmpsRepos.getText()).contains(".") || String.valueOf(nbReps.getText()).contains(".") || String.valueOf(tmpsReposL.getText()).contains(".") || String.valueOf(nbSeance.getText()).contains(".")) {
            //On vérifie (de manière détournée et non-professionelle) que les champs contiennent des entiers
            if (String.valueOf(nbSeance.getText()).contains(".")) {
                nbSeance.setError("Nombre entier requis");
                nbSeance.requestFocus();
            }

            if (String.valueOf(tmpsReposL.getText()).contains(".")) {
                tmpsReposL.setError("Nombre entier requis");
                tmpsReposL.requestFocus();
            }

            if (String.valueOf(nbReps.getText()).contains(".")) {
                nbReps.setError("Nombre entier requis");
                nbReps.requestFocus();
            }

            if (String.valueOf(tmpsRepos.getText()).contains(".")) {
                tmpsRepos.setError("Nombre entier requis");
                tmpsRepos.requestFocus();
            }

            if (String.valueOf(tmpsSport.getText()).contains(".")) {
                tmpsSport.setError("Nombre entier requis");
                tmpsSport.requestFocus();
            }
        } else if (Integer.parseInt(String.valueOf(tmpsSport.getText())) == 0 || Integer.parseInt(String.valueOf(tmpsRepos.getText())) == 0 || Integer.parseInt(String.valueOf(nbReps.getText())) == 0 || Integer.parseInt(String.valueOf(tmpsReposL.getText())) == 0 || Integer.parseInt(String.valueOf(nbSeance.getText())) == 0) {
            //On vérifie que les nombres soient supérieurs à 0

            if (Integer.parseInt(String.valueOf(nbSeance.getText())) == 0) {
                nbSeance.setError("Nombre positif non nul requis");
                nbSeance.requestFocus();
            }

            if (Integer.parseInt(String.valueOf(tmpsReposL.getText())) == 0) {
                tmpsReposL.setError("Nombre positif non nul requis");
                tmpsReposL.requestFocus();
            }

            if (Integer.parseInt(String.valueOf(nbReps.getText())) == 0) {
                nbReps.setError("Nombre positif non nul requis");
                nbReps.requestFocus();
            }

            if (Integer.parseInt(String.valueOf(tmpsRepos.getText())) == 0) {
                tmpsRepos.setError("Nombre positif non nul requis");
                tmpsRepos.requestFocus();
            }

            if (Integer.parseInt(String.valueOf(tmpsSport.getText())) == 0) {
                tmpsSport.setError("Nombre positif non nul requis");
                tmpsSport.requestFocus();
            }
        } else {
            createWorkout(nomExerciceString);
        }
    }

    private void createWorkout(String nomExerciceString) {

        // Vérifier les informations fournies par l'utilisateur
        if (!nameChecked) {
            getExerciceByName(nomExerciceString, id);
        } else {
            final EditText tmpsSport = findViewById(R.id.tempsSport);
            int tempsSportInt = Integer.parseInt(String.valueOf(tmpsSport.getText()));


            final EditText tmpsRepos = findViewById(R.id.tempsRepos);
            int tempsReposInt = Integer.parseInt(String.valueOf(tmpsRepos.getText()));


            final EditText nbReps = findViewById(R.id.reps);
            final int nbRepsInt = Integer.parseInt(String.valueOf(nbReps.getText()));


            final EditText reposLong = findViewById(R.id.reposLong);
            int reposLongInt = Integer.parseInt(String.valueOf(reposLong.getText()));


            final EditText seances = findViewById(R.id.seances);
            final int nbSeancesInt = Integer.parseInt(String.valueOf(seances.getText()));

            //Tous les spinners
            final Spinner spinnerTypeExercice = findViewById(R.id.choixTypeExo);
            final Spinner spinnerTempsEffort = findViewById(R.id.chxEffort);
            final Spinner spinnerTempsRepos = findViewById(R.id.chxRepos);
            final Spinner spinnerTempsReposLong = findViewById(R.id.chxReposLong);

            final TypeExercice typeExercice = (TypeExercice) spinnerTypeExercice.getSelectedItem();
            final String typeTempsSport = (String) spinnerTempsEffort.getSelectedItem();
            final String typeTempsRepos = (String) spinnerTempsRepos.getSelectedItem();
            final String typeTempsReposLong = (String) spinnerTempsReposLong.getSelectedItem();

            tempsSportInt = getTempsEnSecondes(tempsSportInt, typeTempsSport);
            tempsReposInt = getTempsEnSecondes(tempsReposInt, typeTempsRepos);
            reposLongInt = getTempsEnSecondes(reposLongInt, typeTempsReposLong);

            if (id > 0) {
                exerciceSave.modifierExercice(nomExerciceString, tempsSportInt, tempsReposInt, nbRepsInt, reposLongInt, nbSeancesInt, typeExercice);
                modifierExercice(exerciceSave);
            } else {
                saveExercice(nomExerciceString, tempsSportInt, tempsReposInt, nbRepsInt, reposLongInt, nbSeancesInt, typeExercice);
            }
        }
    }


    private int getTempsEnSecondes(int temps, String typeTemps) {
        switch (typeTemps) {
            case "minutes":
                temps = temps * 60;
                break;
            case "heures":
                temps = (temps * 60) * 60;
                break;
        }
        return temps;
    }

    private void saveExercice(String nomExo, int tempsSport, int tempsRepos, int nbReps, int tempsReposLong, int nbSeances, TypeExercice tp) {
        @SuppressLint("StaticFieldLeak")
        class SaveExercice extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {

                // creating a task
                Exercice exo = new Exercice(nomExo, tempsSport, tempsRepos, nbReps, tempsReposLong, nbSeances, tp);
                exo.resetExo();

                // adding to database
                long id = mDb.getAppDatabase().exerciceDao().insert(exo);

                // mettre à jour l'id de la tache
                // Nécessaire si on souhaite avoir accès à l'id plus tard dans l'activité
                exo.setId(id);


                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);

                // Quand l'exercice est créé, on arrête l'activité
                setResult(RESULT_OK);
                finish();
                Toast.makeText(getApplicationContext(), "Exercice créé", Toast.LENGTH_LONG).show();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        SaveExercice st = new SaveExercice();
        st.execute();
    }

    private void modifierExercice(Exercice exo) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class ModifierExercices extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                exo.resetExo();
                mDb.getAppDatabase().exerciceDao().update(exo);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                // Quand l'exercice est modifié, on arrête l'activité
                setResult(RESULT_OK);
                finish();
                Toast.makeText(getApplicationContext(), "Exercice modifié", Toast.LENGTH_LONG).show();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        ModifierExercices gt = new ModifierExercices();
        gt.execute();
    }

    public void onDeleteWorkout(View view) {
        new AlertDialog.Builder(CreateExercice.this).setTitle("Supprimer " + exerciceSave.getNomExercice()).setMessage("Voulez-vous vraiment supprimer l'exercice " + exerciceSave.getNomExercice())

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, (dialog, which) -> supprimerExercice(exerciceSave))

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void supprimerExercice(Exercice exo) {
        ///////////////////////
        // Classe asynchrone permettant de récupérer des taches et de mettre à jour le listView de l'activité
        @SuppressLint("StaticFieldLeak")
        class SupprimerExercices extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {
                mDb.getAppDatabase().exerciceDao().delete(exo);
                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);
                setResult(RESULT_OK);
                finish();
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        SupprimerExercices gt = new SupprimerExercices();
        gt.execute();
    }

}