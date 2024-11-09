![Logo taf](./logo_taf.png)

# Test Automation Framework

TAF est un projet de R&amp;D de cadriciel d’automatisation de test

Il permet l'utilisation de plusieurs outils de tests (Selenium, Gatling, ...) à travers une interface web unique.

L'application côté serveur est une application Java utilisant Springboot.
L'interface est une application web utilisant le framework Angular.

**NOTE**  
Cette documentation n'est pas à jour pour les modules de test de performance Gatling et JMeter.
La documentation à jour (Automne 2024) est [disponible ici](./documentation/README.md).

## Builder et lancer le projet

### Localement :

Il est possible de lancer les applications côté serveur et côté client séparément. Cela est recommandé malgré sa complexité, étant donné qu'ils sont plus facile à débugger de cette manière et que Docker n'est présentement pas fonctionnel.

#### Backend

**Prérequis**
1. Installer Maven
2. Installer Jmeter
3. Installer l'extension `Spring Boot Dashboard`avec VSCode

**Pour tester localement**
1. Naviguer à la racine du projet et ouvrir le fichier `.env` sous la racine du projet et changer la valeur de la variable `JMETER_INSTALL_DIR` pour le chemin où jmeter a été installé. Par exemple: `JMETER_INSTALL_DIR=/Users/jean-francoisl/Downloads/apache-jmeter-5.6.3`

2. Exécuter la commande `mvn clean install`

3. Lancer le module `taf-backend` à travers l'extension VSCode `Spring Boot Dashboard` (vscjava.vscode-spring-boot-dashboard)
La variable d'environnement devrait être chargée.

#### Frontend
- Naviguer dans l;e répertoire `cd /frontend`
- Installez des dépendances avec `npm install`.
- Lancez l'application avec la ligne de commande `npm start`.

L'application devrait être accessible sur http://localhost:4200

### Avec docker :

***NON FONCTIONNEL***
- (Prérequis) Installez Docker ainsi que Docker Compose sur votre système
- Exécutez la commande suivante :
```bash
docker compose --env-file .docker_config.env up
```
***NON FONCTIONNEL***

**Autres services :**  
Pour les autres services, vous pouvez vous référer au Wiki ou contacter les équipes directement.

## Contribuer au projet

La démarche pour contribuer au projet est disponible dans le document [CONTRIBUTING.md](./CONTRIBUTING.md).  
La programmation en JS/TS et Java font usage de conventions précises disponibles dans le fichier [CONVENTIONS.md](./documentation/CONVENTIONS.md). La non-utilisation de ces normes pourrait mener à un refus de vos contributions.

## Contact

En cas de questions, vous pouvez rejoindre [le discord TAF](https://discord.gg/TYrqTdHEqk). Veilliez à ce qu'uniquement 1 ou 2 personnes de votre équipe le rejoigne afin de limiter le nombres de personnes et de faciliter la communication.