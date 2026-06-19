# StackableTools (Édition Kotlin)

[![Fabric API](https://img.shields.io/badge/Loader-Fabric-orange.svg?style=for-the-badge)](https://fabricmc.net/)
[![Licence](https://img.shields.io/badge/License-CC0_1.0-green.svg?style=for-the-badge)](https://github.com/yoanndev90/StackableTools/blob/master/LICENSE)
[![Release GitHub](https://img.shields.io/github/v/release/YoannDev90/StackableTools?style=for-the-badge)](https://github.com/YoannDev90/StackableTools/releases)
[![Téléchargements Modrinth](https://img.shields.io/modrinth/dt/KrqAq3Cj?logo=modrinth&style=for-the-badge)](https://modrinth.com/mod/stackabletools)
[![Téléchargements CurseForge](https://img.shields.io/curseforge/dt/1356234?logo=curseforge&style=for-the-badge)](https://www.curseforge.com/minecraft/mc-mods/stackabletools)



---

- 🇬🇧 English : [README.md](README.md)
- 🇫🇷 Français : ce fichier

**StackableTools** est un puissant mod Minecraft pour Fabric, réécrit en Kotlin, qui rend les outils empilables, révolutionnant la gestion de votre inventaire sans casser l'équilibre du jeu.

## Liens de téléchargement
| Plateforme     | Lien                                                                                      |
| :------------- | :---------------------------------------------------------------------------------------- |
| **CurseForge** | [Télécharger sur CurseForge](https://www.curseforge.com/minecraft/mc-mods/stackabletools) |
| **Modrinth**   | [Télécharger sur Modrinth](https://modrinth.com/mod/stackabletools)                       |

## Support des versions
| Version | Support (Théorique) | Support (Testé complètement) |
| :------ | :------------------ | :--------------------------- |
| 1.20 – 1.20.6 | ✅ | ❌ |
| 1.21.0 – 1.21.3 | ✅ | ❌ |
| 1.21.4 | ✅ | ✅ |
| 1.21.5 – 1.21.11 | ✅ | ❌ |
| 26.1 – 26.1.2 | ✅ | ❌ |

## Fonctionnalités principales
- **Outils empilables** : Épées, pioches, pelles, haches et plus sont désormais empilables !
- **Support des potions** : Empilez vos potions (jusqu'à 16 par défaut).
- **Isolation de la durabilité** : Seul l'objet utilisé subit des dégâts. Le reste de la pile reste intact !
- **Entièrement configurable** : Ajustez la taille des piles pour chaque catégorie (Outils, Potions, Enchantements).
- **Compatible Mending** : Fonctionne parfaitement avec Mending (Raccommodage) et Unbreaking (Solidité).
- **Insertion intelligente** : Les objets ramassés fusionnent automatiquement avec les piles existantes.

## Configuration (Développeur)
> [!IMPORTANT]
> Pour compiler le mod vous-même, vous **devez** d'abord configurer votre environnement.

1.  Ouvrez [compile_config.json](compile_config.json) (ou copiez-le depuis [compile_config_sample.json](compile_config_sample.json)).
2.  Mettez à jour les champs selon votre environnement local :
    ```json
    {
      "output_path": "chemin/vers/votre/sortie/personnalisée",
      "minecraft_version": "1.21.4",
      "fabric_loader_version": "0.16.9"
    }
    ```
3.  Lancez le script de compilation :
    ```bash
    ./compile.sh
    ```

## Installation (Utilisateur)
1.  Téléchargez le dernier `.jar` depuis la page des [Releases](https://github.com/yoanndev90/StackableTools/releases).
2.  Installez **Fabric Loader** et **Fabric API**.
3.  Placez le `.jar` dans votre dossier `mods/`.
4.  Lancez et profitez d'un inventaire plus propre !

> [!TIP] 
> Nous recommandons l'utilisation de **Freesm Launcher** (un fork de Prism) pour une meilleure expérience de modding.

## Prérequis
- **Java 21+** (Cible la dernière JVM stable)
- **Gradle 8.11+**
- **Fabric Loader**

## Contribuer
Les contributions sont ce qui rend la communauté open source si formidable pour apprendre, s'inspirer et créer.
1. Forkez le projet.
2. Créez votre branche de fonctionnalité (`git checkout -b feature/AmazingFeature`).
3. Commitez vos changements (`git commit -m 'Add some AmazingFeature'`).
4. Pushez vers la branche (`git push origin feature/AmazingFeature`).
5. Ouvrez une Pull Request.

## Licence
CC0 1.0 Universal. Voir [LICENSE](LICENSE) pour plus de détails.
