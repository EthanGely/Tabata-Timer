# Tabata-Timer
Le meilleur Tabata timer hors du marché. Vous trouverez forcément mieux sur PlayStore (ou l'AppStore, si vous avez un Iphone).

## Last release notes

### TODO
- [ ] Sons à chaque changement d'action pendant un exercice.
- [ ] Trier les exercices selon la date de création, de modification ou d'execution.
- [ ] Ajouter un racourcit pour lancer le dernier exercice créé, modifié, exécuté.
- [ ] Récupérer l'état d'un exercice, avec la possibilité de reprendre ou de recommencer l'exercice
- [ ] Possibilité d'enlever les effets sonores.
- [ ] Changer la couleur du chrono en fonction du temps restant.

### DONE
- [X] Base de l'application.
- [X] Liste des actions suivantes pendant un exercice.
- [X] Modification, supression d'exercices.
- [X] Gestion des doublons (uniquement sur le nom de l'exercice).
- [X] Système d'étoiles (Nombre de réussite de l'exercice).
- [X] Afficher un message à la fin de l'exercice.
- [X] Possibilité de refaire un exercice après l'avoir fini, sans repasser par l'accueil.

### NEEDS CHECK
- [X] Si les temps sont donnés en minutes/heures vérifier que le chrono soit bien réglé.

## Description technique
- L'application permet de créer des exercices, en spécifiant leur nom, différents temps, nombre de séances, de répétitions, puis de les lancer avec un chrono qui enchaîne automatiquement les tâches à faire.
- Pendant un exercice, une liste déroulante permet de visualiser la prochaine tâche ainsi que sa durée. Cette liste est mise à jour à chaque fin te tâche.
- Chaque exercice peut être modifié ou supprimé.
- Lors de la création ou de la modification d'u exercice, le nom donné sera vérifié. S'il correspond à un nom déjà présent dans la BDD (sauf son propre nom), l'exercice sera sauvegardé avec le nom nomDonné**(1)** (le numéro est incrémenté si nomDonné**(1)** est déjà utilisé.
