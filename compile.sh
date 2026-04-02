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
    if [ -f "compile_config_sample.json" ]; then
        echo "⚠️  $CONFIG_FILE introuvable. Création à partir du sample..."
        cp compile_config_sample.json compile_config.json
        echo "✅ $CONFIG_FILE créé. Merci de le modifier selon tes besoins avant de relancer."
        exit 0
    else
        echo "❌ Erreur : $CONFIG_FILE et son sample sont introuvables."
        exit 1
    fi
fi

# Lecture de la config avec jq
MC_VERSION=$(jq -r '.minecraft_version // empty' "$CONFIG_FILE")
LOADER_VERSION=$(jq -r '.fabric_loader_version // empty' "$CONFIG_FILE")

# Configuration des répertoires
LAUNCHER_DIR=$(jq -r '.launcherDir // empty' "$CONFIG_FILE")
INSTANCE_DIR=$(jq -r '.instanceDir // empty' "$CONFIG_FILE")
LOCATION=$(jq -r '.location // empty' "$CONFIG_FILE")

# Définition du répertoire de destination
if [ -n "$LAUNCHER_DIR" ] && [ "$LAUNCHER_DIR" != "null" ]; then
    DEST_DIR="${LAUNCHER_DIR%/}/${INSTANCE_DIR%/}/${LOCATION%/}/"
else
    echo "❌ Erreur : Configuration incomplète dans $CONFIG_FILE (manque 'launcherDir')."
    exit 1
fi

if [ ! -d "$DEST_DIR" ] && [ "$DRY_RUN" = false ]; then
    if ! mkdir -p "$DEST_DIR"; then
        echo "Erreur : Impossible de créer le répertoire de destination $DEST_DIR."
        exit 1
    fi
    echo "Le répertoire de destination a été créé avec succès."
fi

echo "🚀 Préparation du build pour Minecraft ${MC_VERSION:-inconnu}..."
echo "📂 Destination : $DEST_DIR"
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

    # Trouver le JAR généré (on évite -sources et -dev)
    JAR_PATH=$(ls build/libs/*.jar | grep -v "\-sources" | grep -v "\-dev" | head -n 1)

    if [ -f "$JAR_PATH" ]; then
        if [ "$DRY_RUN" = true ]; then
            echo "Dry run: cp $JAR_PATH $DEST_DIR"
        else
            # Nettoyage avant copie
            BASENAME=$(basename "$JAR_PATH")
            if [ -f "$DEST_DIR/$BASENAME" ]; then
                rm "$DEST_DIR/$BASENAME"
                echo "Ancien fichier $BASENAME supprimé dans la destination."
            fi
            cp "$JAR_PATH" "$DEST_DIR"
            echo "Fichier JAR ($BASENAME) copié dans la destination."
        fi

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
