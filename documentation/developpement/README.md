# Introduction
Test Automation Framework (TAF) est un outil de gestions et d'exécution des plans de tests.

## Architecture
### Existante
Voici le diagramme de composant de l'architecture telle que nous l'avons hérité de la session précédente.

![Diagramme de composants - architecture telle quelle](./architecture/Existant.drawio.svg)

### Envisagée

Après analyse et discussion entre tous les membres de l'équipe, nous avons envisagée une architecture plus modulaire où chaque outil de test de performance est contenu dans son propre module Maven. Cela offre les avantages suivants:
- La maintenance de chaque module est indépendante des autres
- Le backend centralise les dépendances
- De nouveaux modules ou outils de performance peuvent être ajouté facilement
Voici le diagramme de l'architecture envisagée:
![Diagramme de composants - architecture telle quelle](./architecture/Envisagee.drawio.svg)