# Tabata-Timer

Le meilleur Tabata timer hors du marché. Vous trouverez forcément mieux sur PlayStore (ou
l'AppStore, si vous avez un Iphone).

Si vous décidez tout de même d'utiliser cette application (et même si vous ne l'utilisez pas), vous pouvez réaliser un virement sur le compte PayPal à l'adresse "dette-financiere-ethan@gmail.com".
Tous les dons seront récompensés par une durée de reconaissance qui sera proportionnelle à la valeur donnée (10€/minute).

Si, vous ne shouaitez pas faire preuve d'altruisme, ne vous inquiétez pas, cette application à déjà récupéré toutes vos données personelles et les a revendue à prix d'or.

Bon sport !

## Last release notes

### TODO

- [ ] Sons à chaque changement d'action pendant un exercice.
- [ ] Design plus attrayant.
- [ ] Etoile pour favoris (plus de nombre de réussite)
- [ ] Test du con

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
- [X] Possibilité de reprendre ou de recommencer l'exercice.
- [X] Implémenter la classe / table Settings (sauvegarde des parametres utilisateur)
- [X] Possibilité d'enlever les effets sonores.
- [X] Si l'exercice est modifié, on réinitialise la progression (sauf les étoiles).
- [X] Barre de progression

### NEEDS CHECK

- [X] Si les temps sont donnés en minutes/heures vérifier que le chrono soit bien réglé.

## Description de l'applicaton

- L'application permet de créer des exercice, en spécifiant leur nom, différents temps, nombre de
  séances, de répétitions, puis de les lancer avec un chrono et une barre de progression.les tâches à faire sont enchaînées automatiquement.
- Pendant un exercice, une liste déroulante permet de visualiser les prochaines tâches ainsi que leur
  durée. Cette liste est mise à jour à chaque fin te tâche.
- Chaque exercice peut être modifié ou supprimé.
- Lors de la création ou de la modification d'un exercice, le nom donné sera vérifié. S'il correspond
  à un nom déjà présent dans la BDD, l'exercice sera sauvegardé avec le nom
  nomDonné**(1)**. Le numéro est incrémenté si nomDonné**(1)** est déjà utilisé.
- L'utilisateur peut choisir à tout moment de couper le son de l'application. Ce paramètre est stocké en BDD, donc sauvegardé entre les sessions.
- Si un exercice est modifié, la progression (potentielle) de l'utilisateur sera predue.
- Chaque exercice dispose de son compteur de réussite (étoiles). Cette fonctionnalité est assez inutile.
- L'utilisateur à la possibilité de couper les sons de l'application depuis la page d'accueil, ou pendant un exercice.
