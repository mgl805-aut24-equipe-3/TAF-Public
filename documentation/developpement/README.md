# Introduction
Test Automation Framework (TAF) est un outil de gestions et d'exécution des plans de tests.

## Architecture
### Existante
Voici le diagramme de composant de l'architecture telle que nous l'avons hérité de la session précédente.

![Diagramme de composants - architecture telle quelle](./architecture/Existant.drawio.svg)

### Envisagée

Après analyse et discussion entre tous les membres de l'équipe, nous avons envisagée une architecture plus modulaire où chaque outil de test de performance est contenu dans son propre module Maven. Cela offre les avantages suivants:
- La maintenance de chaque module est **indépendante des autres modules de TAF** facilitant ainsi les tests, la livraison et le gestion des versions de chacun des modules.
- Le module **backend centralise les dépendances** afin d'offrir l'accès au différents outils de test de performance
- De nouveaux modules ou outils de performance peuvent être ajoutés facilement par la suite.
Voici le diagramme de l'architecture envisagée:
![Diagramme de composants - architecture telle quelle](./architecture/Envisagee.drawio.svg)

# Gestion de projet
Toutes les tâches du projet (y compris les PR) ont été consignée dans [un tableau kanban](https://github.com/orgs/mgl805-aut24-equipe-3/projects/1/views/1).  
Nous avons utilisé **Discord pour la communication asynchrone** et pour **une réusion journalière** pour le suivi du projet.

# Assurance Qualité
## Processus mis en place
1. **Tâche de programmation collaborative**
   Afin de s'assurer de la qualité de notre code à livrer à l'équipe, nous avons décidé de créer un fork pour toute notre équipe à partir duquel chaque équipier à créer un fork.
   Ainsi, chacun doit créer un Pull Request vers le fork de l'équipe pour que le code soit vérifier avant la fusion dans la branche `develop`. Pour mieux comprendre la configuration, voici un diagramme:
   ![code collaboratif](./architecture/Code%20collaboratif.drawio.svg)
2. **Harmonisation des environnements de développement**
   L'équipe utilise VSCode qui a la possibilité de recommender quelles extensions devraient installer pour faciliter le travail de développement. Par exemple, l'extension Spring Boot Dashboard permet de démarrer ou déboguer l'application backend avec facilité.
3. **Clean-as-you-code**
   Selon les bonnes pratiques, nous utilisons l'extension SonarLint dans VSCode pour s'assurer de la lisibilité et de l'application de règle de style et de syntaxe. Ceci fonctionne à la fois pour Java
4. **Tests unitaires**
   Les composants JMeter, Gatling et la partie UI pour les tests de performance sont maintenant dotés de tests unitaires que chaque équipier peut exécuter avant
5. **Intégration continue spéficique au projet 3**
   Les Github Actions ont été réusiner pour permettre l'exécution des tests automatiques Junit et Karma dès qu'une Pull Request est créée ou modifiée. Cela permet d'effectuer des vérification sans supervision et avant que le code soit vérifier manuellement. 

> **À venir**:  
> Vérification statique avec intégration avec SonarQube  
> Si possible: déploiement continue dans un environnement de tests
## Intégration des changements vers l'équipe 1
Malheureusement, le référentiel principal a changé de visibilité, ce qui a pour conséquence de briser le lien avec le fork de l'équipe. 
La solution est de copier manuellement le code (copier et coller du projet au complet). Le désavantage est que tout l'historique de nos commits sera perdu.

## Déploiement dans un environnement infonuagique
Malheureusement, l'équipe 1 n'a jamais mis d'environnement AWS à notre disposition pour tester nos composants.