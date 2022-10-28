package com.example.tabata_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class Tabata_Timer extends AppCompatActivity {

    private ArrayList<Exercice> listeExercices = new ArrayList<>();

    private Exercice exo1 = new Exercice("exo 1", 30, 15, 10, 30, 2);
    private Exercice exo2 = new Exercice("exo 2", 60, 20, 50, 600, 1);
    private Exercice exo3 = new Exercice("exo 3", 5, 600, 1, 60, 1);
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabata_timer);
        
        listeExercices.add(exo1);
        listeExercices.add(exo2);
        listeExercices.add(exo3);
        for (int i = 0; i < 3; i++) {
            listeExercices.add(exo3);
        }

        LinearLayout linear = findViewById(R.id.linearListeExos);
        LinearLayout fils = null;

        for (int i = 0; i < listeExercices.size(); i++) {


            if (i % 2 == 0) {
                //Création d'un nouveau linearLayout
                id = View.generateViewId();
                fils = new LinearLayout(this);
                fils.setOrientation(LinearLayout.HORIZONTAL);
                fils.setWeightSum(100);
                fils.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
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

        //Définition du texte pour chaque textView
        nomExo.setText(exercice.getNomExercice());
        sportExo.setText(exercice.getSport());
        reposExo.setText(exercice.getRepos());
        repsExo.setText(exercice.getReps());
        reposLongExo.setText(exercice.getReposLong());
        seancesExo.setText(exercice.getSeances());

        if (i % 2 == 0) {
            //Définition des marges (ne marche pas)
            setMargins(layoutExo, 0, 150);
        }else {
            setMargins(layoutExo, 150, 0);
        }

        return linearTmp;
    }

    public static void setMargins (View v, int l, int r) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, 0, r, 0);
            v.requestLayout();
        }
    }
}