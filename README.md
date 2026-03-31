# StackableTools

- Français : [README_FR.md](README_FR.md)
- English : this file

StackableTools is a Minecraft Fabric mod that makes tools stackable for improved inventory management.

## Download links
- https://www.curseforge.com/minecraft/mc-mods/stackabletools
- https://modrinth.com/mod/stackabletools

## Main features
- Stack tools such as swords, pickaxes, shovels, axes, shears, etc.
- Fabric compatibility with Minecraft 1.20.x (to be verified for specific versions)
- Configurable options via `config/stackabletools.json`
- Uses mixins for clean and efficient integration

## Requirements
- Java 20+ (or Java 17 if targeting older Minecraft versions)
- Gradle 8+
- Fabric Loader & Fabric API for client runtime

## User installation
1. Download the `.jar` from the GitHub releases.
2. Place the `.jar` in the `mods/` folder of your Minecraft instance.
3. Launch Minecraft with the Fabric profile.

## Build and run (developer)
```bash
git clone https://github.com/yoann/StackableToolsKotlin.git
cd StackableToolsKotlin
./gradlew build
```
- Output artifact: `build/libs/stackabletoolskotlin-<version>.jar`
- Local run directory: `run/`

## GitHub Actions workflow
- `build`: compile + tests.
- `release`: triggered by `workflow_dispatch` or `master`, creates git tag, GitHub Release, and publishes to CurseForge + Modrinth.
- Ensure secrets are configured:
  - `GITHUB_TOKEN`, `CURSEFORGE_API_KEY`, `MODRINTH_API_TOKEN`,
  - `CURSEFORGE_PROJECT_ID`, `MODRINTH_PROJECT_ID`.

### `workflow_dispatch` inputs
- `release-title` (optional)
- `release-body` (optional)
- `release-tag` (optional)
- `publish-curseforge-modrinth` (true|false)

## Contributing
1. Fork the project.
2. Create a feature/fix branch.
3. Open a PR with description and tests.

## License
MIT. See `LICENSE`.

## Thanks
- Fabric
- SpongePowered Mixin
- Minecraft modding community
