# StackableTools (Kotlin Edition)

[![Fabric API](https://img.shields.io/badge/Loader-Fabric-orange.svg?style=for-the-badge)](https://fabricmc.net/)
[![License](https://img.shields.io/badge/License-CC0_1.0-green.svg?style=for-the-badge)](https://github.com/yoanndev90/StackableTools/blob/master/LICENSE)
[![GitHub Release](https://img.shields.io/github/v/release/YoannDev90/StackableTools?style=for-the-badge)](https://github.com/yoanndev90/StackableTools/releases)
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/KrqAq3Cj?logo=modrinth&style=for-the-badge)](https://modrinth.com/mod/stackabletools)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/1356234?logo=curseforge&style=for-the-badge)](https://www.curseforge.com/minecraft/mc-mods/stackabletools)



---

- 🇫🇷 Français : [README_FR.md](README_FR.md)
- 🇬🇧 English : this file

**StackableTools** is a powerful Minecraft Fabric mod rewritten in Kotlin that makes tools stackable, revolutionizing your inventory management without breaking the game's balance.

## Download Links
| Platform       | Link                                                                                  |
| :------------- | :------------------------------------------------------------------------------------ |
| **CurseForge** | [Download on CurseForge](https://www.curseforge.com/minecraft/mc-mods/stackabletools) |
| **Modrinth**   | [Download on Modrinth](https://modrinth.com/mod/stackabletools)                       |

## Version Support
| Version | Supported (Theoretical) | Supported (Fully Tested) |
| :------ | :---------------------- | :----------------------- |
| 1.20 – 1.20.6 | ✅ | ❌ |
| 1.21.0 – 1.21.3 | ✅ | ❌ |
| 1.21.4 | ✅ | ✅ |
| 1.21.5 – 1.21.11 | ✅ | ❌ |
| 26.1 – 26.1.2 | ✅ | ❌ |

## Main Features
- **Stackable Tools**: Swords, pickaxes, shovels, axes, and more are now stackable!
- **Potions Support**: Stack your potions (up to 16 by default).
- **Durability Isolation**: Only the item you are using takes damage. The rest of the stack stays pristine!
- **Fully Configurable**: Fine-tune stack sizes for each category (Tools, Potions, Enchants).
- **Mending Compatible**: Works perfectly with Mending and Unbreaking.
- **Smart Insertion**: Picked-up items will automatically merge into existing stacks.

## Configuration (Developer)
> [!IMPORTANT]
> To build the mod yourself, you **must** configure your environment first.

1.  Edit [versions.json](versions.json) to configure your local launcher path and instance directories.
2.  Run the build script for all versions:
    ```bash
    ./compile-all.sh
    ```
    Or for a single version, use Gradle directly:
    ```bash
    ./gradlew build -PmcVersion=1.20.4 -Pminecraft_version=1.20.4 -Pyarn_mappings=1.20.4+build.3 -Pfabric_api_version=0.97.3+1.20.4
    ```

## Installation (User)
1.  Download the latest `.jar` from the [Releases](https://github.com/yoanndev90/StackableTools/releases) page.
2.  Install **Fabric Loader** and **Fabric API**.
3.  Place the `.jar` in your `mods/` folder.
4.  Launch and enjoy a cleaner inventory!

> [!TIP] 
> We recommend using **Freesm Launcher** (a fork of Prism) for the best modding experience.

## Requirements
- **Java 21+** (Targeting latest stable JVM)
- **Gradle 8.11+**
- **Fabric Loader**

## Contributing
Contributions are what make the open source community such an amazing place to learn, inspire, and create.
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

## License
CC0 1.0 Universal. See [LICENSE](LICENSE) for details.
