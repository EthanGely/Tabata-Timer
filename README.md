# Tabata-Timer

Le meilleur Tabata timer hors du marché. Vous trouverez forcément mieux sur PlayStore (ou
l'AppStore, si vous avez un Iphone).

## Last release notes

### TODO

- [ ] Sons à chaque changement d'action pendant un settings.
- [ ] Possibilité de reprendre ou de recommencer l'settings
- [ ] Possibilité d'enlever les effets sonores.
- [ ] Changer la couleur du chrono en fonction du temps restant.
- [ ] Implémenter la classe / table Settings (savegarde des paramètres utilisateur)

### DONE

- [X] Base de l'application.
- [X] Liste des actions suivantes pendant un settings.
- [X] Modification, supression d'settings.
- [X] Gestion des doublons (uniquement sur le nom de l'settings).
- [X] Système d'étoiles (Nombre de réussite de l'settings).
- [X] Afficher un message à la fin de l'settings.
- [X] Possibilité de refaire un settings après l'avoir fini, sans repasser par l'accueil.
- [X] Possibilité d'utiliser un racourci pour lancer le dernier settings créé, modifié, exécuté.
- [X] Trier les settings selon la date de création, de modification ou d'execution.
- [X] Reprends automatiquement l'settings là où il a été laissé.

### NEEDS CHECK

- [X] Si les temps sont donnés en minutes/heures vérifier que le chrono soit bien réglé.

## Description technique

- L'application permet de créer des settings, en spécifiant leur nom, différents temps, nombre de
  séances, de répétitions, puis de les lancer avec un chrono qui enchaîne automatiquement les tâches
  à faire.
- Pendant un settings, une liste déroulante permet de visualiser la prochaine tâche ainsi que sa
  durée. Cette liste est mise à jour à chaque fin te tâche.
- Chaque settings peut être modifié ou supprimé.
- Lors de la création ou de la modification d'u settings, le nom donné sera vérifié. S'il correspond
  à un nom déjà présent dans la BDD (sauf son propre nom), l'settings sera sauvegardé avec le nom
  nomDonné**(1)** (le numéro est incrémenté si nomDonné**(1)** est déjà utilisé.
