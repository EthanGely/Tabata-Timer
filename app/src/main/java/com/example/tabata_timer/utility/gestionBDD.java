package com.example.tabata_timer.utility;

import android.os.AsyncTask;

import com.example.tabata_timer.database.DatabaseClient;
import com.example.tabata_timer.database.Exercice;

import java.util.List;

public class gestionBDD {

    private DatabaseClient mDb;
    private Exercice exerciceClass;
    private List<Exercice> exercicesClass;


    public gestionBDD(DatabaseClient mDb) {
        this.mDb = mDb;
    }

    public Exercice getExercice() {
        return exerciceClass;
    }

    public List<Exercice> getExercicesClass() {
        return exercicesClass;
    }

    public void getExercice(int id) {
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
                exerciceClass = exercice;
            }
        }
        GetExercice gt = new GetExercice();
        gt.execute();
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
                exercicesClass = exercices;
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
            }
        }

        //////////////////////////
        // IMPORTANT bien penser à executer la demande asynchrone
        // Création d'un objet de type GetTasks et execution de la demande asynchrone
        SupprimerExercices gt = new SupprimerExercices();
        gt.execute();
    }
}
