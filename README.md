# StackableTools

StackableTools est un mod Minecraft développé avec Fabric qui permet de rendre les outils stackables, améliorant ainsi la gestion de l'inventaire et l'expérience de jeu.

## Fonctionnalités
- Permet de stacker plusieurs outils identiques dans un même slot d'inventaire.
- Compatible avec les outils vanilla (pioche, pelle, hache, épée, etc.).
- Configuration simple et extensible.

## Installation (Utilisateur)
1. Téléchargez le fichier `.jar` du mod depuis la section releases ou compilez-le vous-même (voir ci-dessous).
2. Installez [Fabric Loader](https://fabricmc.net/use/) et [Fabric API](https://modmuss50.me/fabric.html).
3. Placez le fichier `.jar` dans le dossier `mods` de votre installation Minecraft.

## Compilation et lancement (Développeur)
1. Clonez le dépôt :
   ```bash
   git clone https://github.com/yoann/StackableTools.git
   ```
2. Ouvrez le projet dans IntelliJ IDEA ou VSCode.
3. Compilez le mod avec Gradle :
   ```bash
   ./gradlew build
   ```
4. Le fichier compilé se trouve dans `build/libs/`.

## Structure du projet
- `src/main/java/org/yoann/stackabletools/` : Code principal du mod
- `src/main/resources/` : Ressources Fabric (mod.json, assets, mixins)
- `src/client/java/org/yoann/stackabletools/` : Code client (mixin client, etc.)
- `run/` : Dossier de lancement local

## Contribuer
Les contributions sont les bienvenues !
- Forkez le projet
- Créez une branche pour votre fonctionnalité/correction
- Soumettez une Pull Request

## Licence
Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus d'informations.

## Remerciements
- [Fabric](https://fabricmc.net/)
- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin)
