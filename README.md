![Logo taf](./logo_taf.png)

# Test Automation Framework

TAF est un projet de R&amp;D de cadriciel d’automatisation de test


Il permet l'utilisation de plusieurs outils de tests (Selenium, Gatling, ...) à travers une interface web unique.

L'application côté serveur est une application Java utilisant Springboot.
L'interface est une application web utilisant le framework Angular.

## Builder et lancer le projet

Pour lancer le projet, la méthode la plus simple est d'utiliser

### Avec docker :

- (Prérequis) Installez Docker ainsi que Docker Compose sur votre système
- Exécutez la commande suivante :
```bash
docker compose --env-file .docker_config.env up
```

### Sans docker :

Il est aussi possible de lancer les applications côté serveur et côté client séparément. Cela n'est cependant pas recommandé, car cela est plus complexe, et le deviendra de plus en plus à mesure que de nouveaux services seront ajouté.

#### Backend

**Prérequis**
1. Installer Jmeter
   Faire un download du ZIP ou TGZ et l'extraire dans n'importe quel dossier
2. Installer Maven

**Pour tester localement**
1. Ouvrir le fichier `.env` sous la racine du projet et changer la valeur de la variable `JMETER_INSTALL_DIR` pour le chemin où jmeter a été installé. Par exemple: `JMETER_INSTALL_DIR=/Users/jean-francoisl/Downloads/apache-jmeter-5.6.3`

2. Lancer l'application SpringBoot en utilisant la vue Web VSCode `Sprint Boot Dashboard`
La variable d'environnement devrait être loader.

#### Frontend
- Installez des dépendances avec `npm install`.
- Lancez l'application avec la ligne de commande
`npm start`.

**Autres services :**  
Pour les autres services, vous pouvez vous référer au Wiki ou contacter les équipes directement.

## Contribuer au projet

La démarche pour contribuer au projet est disponible dans le document [CONTRIBUTING.md](./CONTRIBUTING.md).  
La programmation en JS/TS et Java font usage de conventions précises disponibles dans le fichier [CONVENTIONS.md](./documentation/CONVENTIONS.md). La non-utilisation de ces normes pourrait mener à un refus de vos contributions.

## Contact

En cas de questions, vous pouvez rejoindre [le discord TAF](https://discord.gg/TYrqTdHEqk). Veilliez à ce qu'uniquement 1 ou 2 personnes de votre équipe le rejoigne afin de limiter le nombres de personnes et de faciliter la communication.