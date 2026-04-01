# StackableTools

- English : [README.md](README.md)
- Français : ce fichier

StackableTools est un mod Minecraft pour Fabric qui permet de stacker les outils et ainsi améliorer la gestion de l'inventaire.

## Liens de téléchargement
- https://www.curseforge.com/minecraft/mc-mods/stackabletools
- https://modrinth.com/mod/stackabletools

## Fonctionnalités principales
- Stack lames, pioches, pelles, haches, cisailles, etc.
- Compatibilité Fabric avec Minecraft 1.20.x (à vérifier selon la version du mod)
- Configuration enrichie via `config/stackabletools.json`
- Mixin pour intégration propre et performante

## Prérequis
- Java 20+ (ou 17 selon cible mod Minecraft)
- Gradle 8+
- Fabric Loader & Fabric API pour le client

## Installation utilisateur
1. Téléchargez le `.jar` depuis les releases GitHub.
2. Placez le `.jar` dans `mods/` de votre instance Minecraft.
3. Lancez Minecraft avec le profil Fabric.

## Configuration facile
Le fichier de config est généré dans `config/stackabletoolskotlin.toml` (ou `src/main/resources/stackabletoolskotlin.default.toml` par défaut).

Options disponibles :
- `logging.enable` (true/false)
- `logging.level` (`TRACE`, `DEBUG`, `INFO`, `WARN`, `ERROR`)
- `stacking.enable` (true/false)
- `stacking.max_stack_size` (64 par défaut)
- `stacking.max_tool_stack_size` (8 par défaut)
- `stacking.max_potion_stack_size` (16 par défaut)
- `stacking.active_categories` (`["tools","potions"]` par défaut)
- `stacking.manual_item_ids` (liste de `minecraft:item_id`)
- `stacking.excluded_item_ids` (liste de `minecraft:item_id`)

Exemples :
```toml
stacking.enable = true
stacking.active_categories = ["tools", "potions", "armor"]
stacking.manual_item_ids = ["minecraft:shield", "minecraft:elytra"]
stacking.excluded_item_ids = ["minecraft:stone_axe"]
```

> Astuce : la forme courte (`"diamond_hoe"`) est supportée pour les items personnalisés pour plus de facilité.

> [!TIP] 
> Utilisez Freesm Launcher (fork de Prism) pour une installation et une gestion de modpack optimisées.

## Compilation et exécution (développeur)
```bash
git clone https://github.com/yoann/StackableToolsKotlin.git
cd StackableToolsKotlin
./gradlew build
```
- Artéfact généré : `build/libs/stackabletoolskotlin-<version>.jar`
- Dossier d’exécution local : `run/`

## Workflow GitHub Actions
- `build` : compilation + tests.
- `release` : trigger `workflow_dispatch` ou `master`, tag git, release GitHub + publication CurseForge/Modrinth.
- Vérifiez que les secrets sont configurés :
  - `GITHUB_TOKEN`, `CURSEFORGE_API_KEY`, `MODRINTH_API_TOKEN`,
  - `CURSEFORGE_PROJECT_ID`, `MODRINTH_PROJECT_ID`.

### Inputs de `workflow_dispatch`
- `release-title` (optionnel)
- `release-body` (optionnel)
- `release-tag` (optionnel)
- `publish-curseforge-modrinth` (true|false)

## Contribuer
1. Fork.
2. Branche feature/fix.
3. PR avec description et tests.

## Licence
CC0 1.0 Universal. Voir [LICENSE](https://github.com/YoannDev90/StackableToolsKotlin/blob/master/LICENSE)

## Merci
- Fabric
- SpongePowered Mixin
- Communauté Minecraft modding
