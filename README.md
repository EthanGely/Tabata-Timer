# Tabata-Timer

Le meilleur Tabata timer hors du marché. Vous trouverez forcément mieux sur PlayStore (ou
l'AppStore, si vous avez un Iphone).

## Last release notes

### TODO

- [ ] Sons à chaque changement d'action pendant un exercice.
- [ ] Possibilité de reprendre ou de recommencer l'exercice
- [ ] Possibilité d'enlever les effets sonores.
- [ ] Changer la couleur du chrono en fonction du temps restant.
- [ ] Implémenter la classe / table Settings (sauvegarde des parametres utilisateur)

### DONE

- [X] Base de l'application.
- [X] Liste des actions suivantes pendant un exercice.
- [X] Modification, supression d'exercice.
- [X] Gestion des doublons (uniquement sur le nom de l'exercice).
- [X] Système d'étoiles (Nombre de réussite de l'exercice).
- [X] Afficher un message à la fin de l'exercice.
- [X] Possibilité de refaire un settings après l'avoir fini, sans repasser par l'accueil.
- [X] Possibilité d'utiliser un racourci pour lancer le dernier exercice créé, modifié, exécuté.
- [X] Trier les settings selon la date de création, de modification ou d'execution.
- [X] Reprends automatiquement l'exercice là où il a été laissé.
- [X] Le bouton reprendre est désactivé s'il n'existe aucun exercice.

### NEEDS CHECK

- [X] Si les temps sont donnés en minutes/heures vérifier que le chrono soit bien réglé.

## Description technique

- L'application permet de créer des exercice, en spécifiant leur nom, différents temps, nombre de
  séances, de répétitions, puis de les lancer avec un chrono qui enchaîne automatiquement les tâches
  à faire.
- Pendant un exercice, une liste déroulante permet de visualiser les prochaines tâches ainsi que leur
  durée. Cette liste est mise à jour à chaque fin te tâche.
- Chaque exercice peut être modifié ou supprimé.
- Lors de la création ou de la modification d'un exercice, le nom donné sera vérifié. S'il correspond
  à un nom déjà présent dans la BDD, l'exercice sera sauvegardé avec le nom
  nomDonné**(1)**. Le numéro est incrémenté si nomDonné**(1)** est déjà utilisé.
