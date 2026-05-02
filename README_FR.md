# 🛠️ StackableTools (Édition Kotlin)

[![Version Minecraft](https://img.shields.io/badge/Minecraft-1.20-1.20.4-blue.svg?style=for-the-badge&logo=minecraft)](https://www.minecraft.net/)
[![Fabric API](https://img.shields.io/badge/Loader-Fabric-orange.svg?style=for-the-badge)](https://fabricmc.net/)
[![Licence](https://img.shields.io/badge/License-CC0_1.0-green.svg?style=for-the-badge)](https://github.com/yoann/StackableTools/blob/master/LICENSE)
[![Release GitHub](https://img.shields.io/github/v/release/yoann/StackableTools?style=for-the-badge)](https://github.com/yoann/StackableTools/releases)

---

- 🇬🇧 English : [README.md](README.md)
- 🇫🇷 Français : ce fichier

**StackableTools** est un puissant mod Minecraft pour Fabric, réécrit en Kotlin, qui rend les outils empilables (stackables), révolutionnant la gestion de votre inventaire sans casser l'équilibre du jeu.

## 📥 Liens de téléchargement
| Plateforme     | Lien                                                                                      |
| :------------- | :---------------------------------------------------------------------------------------- |
| **CurseForge** | [Télécharger sur CurseForge](https://www.curseforge.com/minecraft/mc-mods/stackabletools) |
| **Modrinth**   | [Télécharger sur Modrinth](https://modrinth.com/mod/stackabletools)                       |

## 🎮 Support des versions
| Version Minecraft | Fabric Loader | Fabric API | Statut       | Remarques                             |
| :---------------- | :------------ | :--------- | :----------- | :------------------------------------ |
| 1.20.0            | ≥0.18.5       | ≥0.97.0    | ✅ Compatible | Testé par la communauté (non vérifié) |
| 1.20.1            | ≥0.18.5       | ≥0.97.0    | ✅ Compatible | Testé par la communauté (non vérifié) |
| 1.20.2            | ≥0.18.5       | ≥0.97.0    | ✅ Compatible | Testé par la communauté (non vérifié) |
| 1.20.3            | ≥0.18.5       | ≥0.97.0    | ✅ Compatible | Testé par la communauté (non vérifié) |
| 1.20.4            | ≥0.18.5       | ≥0.97.3    | ✅ **Testé**  | Complètement testé & recommandé       |
| 1.20.5+           | ≥0.18.5       | ≥1.0.0     | ⚠️ Non testé  | Peut nécessiter des mises à jour      |

## ✨ Fonctionnalités principales
- ⚔️ **Outils empilables** : Épées, pioches, pelles, haches et plus sont désormais empilables !
- 🧪 **Support des potions** : Empilez vos potions (jusqu'à 16 par défaut).
- 🛡️ **Isolation de la durabilité** : Seul l'objet utilisé subit des dégâts. Le reste de la pile reste intact !
- ⚙️ **Entièrement configurable** : Ajustez la taille des piles pour chaque catégorie.
- 🔄 **Compatible Mending** : Fonctionne parfaitement avec Mending (Raccommodage) et Unbreaking (Solidité).
- 🏗️ **Insertion intelligente** : Les objets ramassés fusionnent automatiquement avec les piles existantes.

## ⚙️ Configuration (Développeur)
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

## 🚀 Installation (Utilisateur)
1.  Téléchargez le dernier `.jar` depuis la page des [Releases](https://github.com/yoann/StackableTools/releases).
2.  Installez **Fabric Loader** et **Fabric API**.
3.  Placez le `.jar` dans votre dossier `mods/`.
4.  Lancez et profitez d'un inventaire plus propre !

> [!TIP] 
> Nous recommandons l'utilisation de **Freesm Launcher** (un fork de Prism) pour une meilleure expérience de modding.

## 🛠️ Prérequis
- ☕ **Java 21+** (Cible la dernière JVM stable)
- 🐘 **Gradle 8.11+**
- 🧶 **Fabric Loader**

## ⚙️ Configuration facile
Le fichier de configuration est généré dans `config/stackabletools.toml`.

Options disponibles :
- `logging.enable` (true/false)
- `stacking.max_stack_size` (64 par défaut)
- `stacking.max_tool_stack_size` (8 par défaut)
- `stacking.max_potion_stack_size` (16 par défaut)
- `stacking.active_categories` (`["tools","potions"]` par défaut)

## 🤝 Contribuer
Les contributions rendent la communauté open source formidable.
1. Forkez le projet.
2. Créez votre branche de fonctionnalité (`git checkout -b feature/AmazingFeature`).
3. Commitez vos changements (`git commit -m 'Add some AmazingFeature'`).
4. Pushez vers la branche (`git push origin feature/AmazingFeature`).
5. Ouvrez une Pull Request.

## 📜 Licence
CC0 1.0 Universal. Voir [LICENSE](LICENSE) pour plus de détails.

---
*Fait avec ❤️ par YoannDev90*
