package com.example.tabata_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.Exercice;
import com.example.tabata_timer.database.ExerciceDao;

import java.util.List;

public class CreateExercice extends AppCompatActivity {

    // DATA
    private DatabaseClient mDb;

    public static final String MODIFIER_KEY = "modifier_key";
    public static final String EXERCICE_KEY = "exercice_key";

    private Exercice exerciceSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exercice);

        boolean isModifier = (boolean) getIntent().getSerializableExtra(MODIFIER_KEY);

        // Récupération du DatabaseClient
        mDb = DatabaseClient.getInstance(getApplicationContext());

        final Spinner spinnerTempsEffort = (Spinner) findViewById(R.id.chxEffort);
        final Spinner spinnerTempsRepos = (Spinner) findViewById(R.id.chxRepos);
        final Spinner spinnerTempsReposLong = (Spinner) findViewById(R.id.chxReposLong);

        String[] typesTemps={"secondes","minutes", "heures"};
        ArrayAdapter<String> dataAdapterR = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,typesTemps);
        dataAdapterR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTempsEffort.setAdapter(dataAdapterR);
        spinnerTempsRepos.setAdapter(dataAdapterR);
        spinnerTempsReposLong.setAdapter(dataAdapterR);

        if (isModifier) {
            int id = (int) getIntent().getIntExtra(EXERCICE_KEY, 2);
            //Récupère et affiche les données de l'exercice
            getExercice(id);
            
            
        }
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


    public void setSpinnerValue(Spinner sp, int secondes) {
        ArrayAdapter myAdap = (ArrayAdapter) sp.getAdapter(); //cast to an ArrayAdapter

        int spinnerPosition = myAdap.getPosition(exerciceSave.getTypeOfTime(secondes));
        sp.setSelection(spinnerPosition);
    }

    private void addExercice() {
        EditText nomExo = findViewById(R.id.nomExo);
        nomExo.setText(exerciceSave.getNomExercice());


        EditText tempsSport = findViewById(R.id.tempsSport);
        tempsSport.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getSport())));
        setSpinnerValue(findViewById(R.id.chxEffort), exerciceSave.getSport());


        EditText tempsRepos = findViewById(R.id.tempsRepos);
        tempsRepos.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getRepos())));

        setSpinnerValue(findViewById(R.id.chxRepos), exerciceSave.getRepos());


        EditText reps = findViewById(R.id.reps);
        reps.setText(String.valueOf(exerciceSave.getRepetitions()));


        EditText reposLong = findViewById(R.id.reposLong);
        reposLong.setText(String.valueOf(exerciceSave.getBestTime(exerciceSave.getReposLong())));
        setSpinnerValue(findViewById(R.id.chxReposLong), exerciceSave.getReposLong());


        EditText seances = findViewById(R.id.seances);
        seances.setText(String.valueOf(exerciceSave.getSeances()));
    }

    public void onCancel(View view) {
        finish();
    }

    public void onCreateWorkout(View view) {
        //Tous les editText
        final EditText nomExo = (EditText) findViewById(R.id.nomExo);
        final String nomExerciceString = String.valueOf(nomExo.getText());
        // Vérifier les informations fournies par l'utilisateur
        if (nomExerciceString.isEmpty()) {
            nomExo.setError("Nom requis");
            nomExo.requestFocus();
            return;
        }


        final EditText tmpsSport = (EditText) findViewById(R.id.tempsSport);
        int tempsSportInt = Integer.parseInt(String.valueOf(tmpsSport.getText()));


        final EditText tmpsRepos = (EditText) findViewById(R.id.tempsRepos);
        int tempsReposInt = Integer.parseInt(String.valueOf(tmpsRepos.getText()));


        final EditText nbReps = (EditText) findViewById(R.id.reps);
        final int nbRepsInt = Integer.parseInt(String.valueOf(nbReps.getText()));


        final EditText reposLong = (EditText) findViewById(R.id.reposLong);
        int reposLongInt = Integer.parseInt(String.valueOf(reposLong.getText()));


        final EditText seances = (EditText) findViewById(R.id.seances);
        final int nbSeancesInt = Integer.parseInt(String.valueOf(seances.getText()));

        //Tous les spinners
        final Spinner spinnerTempsEffort = (Spinner) findViewById(R.id.chxEffort);
        final Spinner spinnerTempsRepos = (Spinner) findViewById(R.id.chxRepos);
        final Spinner spinnerTempsReposLong = (Spinner) findViewById(R.id.chxReposLong);

        final String typeTempsSport = (String) spinnerTempsEffort.getSelectedItem();
        final String typeTempsRepos = (String) spinnerTempsRepos.getSelectedItem();
        final String typeTempsReposLong = (String) spinnerTempsReposLong.getSelectedItem();

        tempsSportInt = getTempsEnSecondes(tempsSportInt, typeTempsSport);
        tempsReposInt = getTempsEnSecondes(tempsReposInt, typeTempsRepos);
        reposLongInt = getTempsEnSecondes(reposLongInt, typeTempsReposLong);


        /**
         * Création d'une classe asynchrone pour sauvegarder la tache donnée par l'utilisateur
         */
        int finalTempsSportInt = tempsSportInt;
        int finalTempsReposInt = tempsReposInt;
        int finalReposLongInt = reposLongInt;

        class SaveExercice extends AsyncTask<Void, Void, Exercice> {

            @Override
            protected Exercice doInBackground(Void... voids) {

                // creating a task
                Exercice exo = new Exercice(nomExerciceString, finalTempsSportInt, finalTempsReposInt, nbRepsInt, finalReposLongInt, nbSeancesInt);

                // adding to database
                long id = mDb.getAppDatabase()
                        .exerciceDao()
                        .insert(exo);

                // mettre à jour l'id de la tache
                // Nécessaire si on souhaite avoir accès à l'id plus tard dans l'activité
                exo.setId(id);


                return exo;
            }

            @Override
            protected void onPostExecute(Exercice exercice) {
                super.onPostExecute(exercice);

                // Quand la tache est créée, on arrête l'activité AddTaskActivity (on l'enleve de la pile d'activités)
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


    private int getTempsEnSecondes(int temps, String typeTemps) {
        switch (typeTemps) {
            case "minutes":
                temps = temps * 60;
                break;
            case "heures" :
                temps = (temps * 60) * 60;
                break;
        }
        return temps;
    }
    
}