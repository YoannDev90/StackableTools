# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-10-05

### Added
- Configuration file support (`config/stackabletools.json`)
- Configurable maximum stack size (default: 8, range: 1-64)
- Optional debug logging setting in config
- Proper SLF4J logging system with different log levels (debug, info, error)
- Mod icon now displays in the mods menu and on the JAR file
- CONFIG.md documentation for configuration options

### Changed
- All comments translated from French to English
- Replaced `System.out.println` with proper logger calls
- Updated mod description to mention configurable stack size
- Debug logging is now conditional based on config setting

### Fixed
- Improved code documentation with English comments throughout

## [1.0.0] - Initial Release

### Added
- Initial release of Stackable Tools mod
- Tools can stack up to 8 items
- Automatic separation of damaged tools from intact tool stacks
- Intact tools are returned to player inventory when a tool is damaged
- Support for all tool types (pickaxes, axes, shovels, hoes, swords)

