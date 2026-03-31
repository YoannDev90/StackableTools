#!/bin/bash

# Build script options
# -c : clean before compile
# -h : show help
# -v : show script version
# -d : enable debug mode (set -x)
# -w : disable Gradle warnings (add --warning-mode=none)
# -n : do not use the Gradle daemon (useful to avoid state/cache issues during development)
# -u : refresh dependencies before building (gradle --refresh-dependencies)
# -p : generate performance profile (gradle build --profile)
# -r : run clean + build + advanced logs
# -l : log output into a file (gradle ... | tee log-file)
# -k : keep going on task failures (gradle --continue)
# -y : auto-confirm destructive actions (delete existing jar etc.)
# --dry-run : simulate actions without filesystem changes (test config path, no copy)

SCRIPT_VERSION="1.0.0"

usage() {
    echo "Usage: $0 [-c] [-h] [-v] [-d] [-w] [-n] [-u] [-p] [-r] [-l log-file] [-k] [-y] [--dry-run]"
    echo "  -c  : clean Gradle before build"
    echo "  -h  : display this help message"
    echo "  -v  : display script version"
    echo "  -d  : enable debug mode (set -x)"
    echo "  -w  : disable Gradle warnings (--warning-mode=none)"
    echo "  -n  : do not use Gradle daemon (--no-daemon)"
    echo "  -u  : refresh Gradle dependencies (--refresh-dependencies)"
    echo "  -p  : generate build profile (--profile)"
    echo "  -r  : run rebuild (clean build + advanced logs)"
    echo "  -l  : log output to file (use argument e.g. -l build.log)"
    echo "  -k  : keep going on task failures (--continue)"
    echo "  -y  : auto-confirm destructive operations"
    echo "  --dry-run : run in simulation mode (no file operations)"
    exit 0
}

version() {
    echo "$0 version $SCRIPT_VERSION"
    exit 0
}

CLEAN=false
DEBUG=false
NO_DAEMON=false
WARN=false
REFRESH_DEPS=false
PROFILE=false
REBUILD=false
LOG_FILE=""
KEEP_GOING=false
AUTO_YES=false
DRY_RUN=false

# Use getopt for long option support
PARSED=$(getopt -o chvdwnupkyl: --long dry-run -- "$@")
if [ $? -ne 0 ]; then
    usage
fi

eval set -- "$PARSED"

while true; do
    case "$1" in
        -c) CLEAN=true; shift ;; 
        -h) usage ;; 
        -v) version ;; 
        -d) DEBUG=true; shift ;; 
        -w) WARN=true; shift ;; 
        -n) NO_DAEMON=true; shift ;; 
        -u) REFRESH_DEPS=true; shift ;; 
        -p) PROFILE=true; shift ;; 
        -r) REBUILD=true; shift ;; 
        -k) KEEP_GOING=true; shift ;; 
        -y) AUTO_YES=true; shift ;; 
        -l) LOG_FILE="$2"; shift 2 ;; 
        --dry-run) DRY_RUN=true; shift ;; 
        --) shift; break ;; 
        *) usage ;; 
    esac
done

$DEBUG && set -x

clear

CONFIG_FILE="compile_config.json"
if [ ! -f "$CONFIG_FILE" ]; then
    echo "Erreur : Le fichier de configuration $CONFIG_FILE est introuvable."
    exit 1
fi

LAUNCHER_DIR=$(jq -r '.launcherDir' "$CONFIG_FILE")
INSTANCE_DIR=$(jq -r '.instanceDir' "$CONFIG_FILE")
LOCATION=$(jq -r '.location' "$CONFIG_FILE")

if [ -z "$LAUNCHER_DIR" ] || [ -z "$INSTANCE_DIR" ] || [ -z "$LOCATION" ]; then
    echo "Erreur : chemins de configuration invalides dans $CONFIG_FILE."
    exit 1
fi

DEST_DIR="${LAUNCHER_DIR%/}/${INSTANCE_DIR%/}/${LOCATION%/}/"

if [ ! -d "$DEST_DIR" ]; then
    if ! mkdir -p "$DEST_DIR"; then
        echo "Erreur : Impossible de créer le répertoire de destination $DEST_DIR."
        exit 1
    fi
    echo "Le répertoire de destination a été créé avec succès."
fi

echo "Compilation du projet... $(date '+%T')"

if [ "$REBUILD" = true ]; then
    CLEAN=true
fi

if [ "$CLEAN" = true ] && [ "$DRY_RUN" = false ]; then
    echo "-- clean avant compilation --"
    ./gradlew clean ${NO_DAEMON:+--no-daemon} || { echo "Erreur : clean échoué"; exit 1; }
fi

GRADLE_CMD=(./gradlew)
[ "$NO_DAEMON" = true ] && GRADLE_CMD+=(--no-daemon)
[ "$WARN" = true ] && GRADLE_CMD+=(--warning-mode=none)
[ "$KEEP_GOING" = true ] && GRADLE_CMD+=(--continue)
[ "$REFRESH_DEPS" = true ] && GRADLE_CMD+=(--refresh-dependencies)
[ "$PROFILE" = true ] && GRADLE_CMD+=(--profile)

GRADLE_CMD+=(build)

if [ "$DRY_RUN" = true ]; then
    echo "Dry run: " "${GRADLE_CMD[@]}"
    echo "Dry run mode: no file operations will be performed."
    EXIT_CODE=0
else
    if [ -n "$LOG_FILE" ]; then
        "${GRADLE_CMD[@]}" 2>&1 | tee "$LOG_FILE"
        EXIT_CODE=${PIPESTATUS[0]}
    else
        "${GRADLE_CMD[@]}"
        EXIT_CODE=$?
    fi
fi

if [ "$EXIT_CODE" -eq 0 ]; then
    echo "Compilation terminée avec succès ! $(date '+%T')"
    JAR_PATH="build/libs/stackabletoolskotlin-1.0.0.jar"

    if [ -f "$JAR_PATH" ]; then
        if [ -f "$DEST_DIR/stackabletoolskotlin-1.0.0.jar" ]; then
            rm "$DEST_DIR/stackabletoolskotlin-1.0.0.jar"
            echo "Ancien fichier supprimé."
        fi
        mv "$JAR_PATH" "$DEST_DIR"
        echo "Fichier JAR déplacé vers la destination."
        if [ "$RUN_TESTS" = true ]; then
            echo "Exécution des tests..."
            ./gradlew test ${NO_DAEMON:+--no-daemon} ${WARN:+--warning-mode=none} || { echo "Tests échoués"; exit 1; }
            echo "Tests terminés avec succès."
        fi
    else
        echo "Erreur : Le fichier JAR n'a pas été généré."
        exit 1
    fi
else
    echo "Erreur : La compilation a échoué."
    exit 1
fi
