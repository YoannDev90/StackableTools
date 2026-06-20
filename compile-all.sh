#!/bin/bash

# Multi-version build script
# Builds all Minecraft versions defined in versions.json
# Usage: ./compile-all.sh [-c] [-w] [-n] [--dry-run]
#   -c : clean before each build
#   -w : disable Gradle warnings
#   -n : no Gradle daemon
#   --dry-run : simulate without building

SCRIPT_VERSION="1.1.0"
CONFIG_FILE="versions.json"
CLEAN=false
WARN=false
NO_DAEMON=false
DRY_RUN=false

usage() {
    echo "Usage: $0 [-c] [-w] [-n] [--dry-run]"
    echo "  -c : clean before each build"
    echo "  -w : disable Gradle warnings"
    echo "  -n : no Gradle daemon"
    echo "  --dry-run : simulate without building"
    exit 0
}

PARSED=$(getopt -o cwnh --long dry-run -n "$0" -- "$@")
if [ $? -ne 0 ]; then usage; fi
eval set -- "$PARSED"
while true; do
    case "$1" in
        -c) CLEAN=true; shift ;;
        -w) WARN=true; shift ;;
        -n) NO_DAEMON=true; shift ;;
        --dry-run) DRY_RUN=true; shift ;;
        -h|--help) usage ;;
        --) shift; break ;;
        *) usage ;;
    esac
done

if [ ! -f "$CONFIG_FILE" ]; then
    echo "Error: $CONFIG_FILE not found."
    exit 1
fi

LAUNCHER_DIR=$(jq -r '.launcherDir // empty' "$CONFIG_FILE")
LOCATION=$(jq -r '.location // empty' "$CONFIG_FILE")
VERSIONS=$(jq -c '.versions[]' "$CONFIG_FILE")

if [ -z "$VERSIONS" ]; then
    echo "Error: no versions defined in $CONFIG_FILE"
    exit 1
fi

echo "=========================================="
echo "  StackableTools - Multi-version Builder  "
echo "=========================================="
echo ""

BUILD_ALL_SUCCESS=true

while read -r version_data; do
    MC_VERSION=$(echo "$version_data" | jq -r '.version')
    MC_SRC=$(echo "$version_data" | jq -r '.mcVersion // .version')
    MINECRAFT_VER=$(echo "$version_data" | jq -r '.minecraft_version')
    YARN=$(echo "$version_data" | jq -r '.yarn_mappings // empty')
    FABRIC_API=$(echo "$version_data" | jq -r '.fabric_api_version')
    LOADER=$(echo "$version_data" | jq -r '.loader_version // empty')

    if [ ! -d "src/mc-${MC_SRC}" ]; then
        echo "--- Skipping Minecraft $MC_VERSION (no src/mc-${MC_SRC}) ---"
        echo ""
        continue
    fi

    echo "--- Building for Minecraft $MC_VERSION ($MINECRAFT_VER) ---"

    if [ "$DRY_RUN" = true ]; then
        PROPS="-DmcVersion=$MC_SRC -PmcVersion=$MC_SRC -Pminecraft_version=$MINECRAFT_VER -Pfabric_api_version=$FABRIC_API"
        [ -n "$YARN" ] && PROPS="$PROPS -Pyarn_mappings=$YARN"
        [ -n "$LOADER" ] && PROPS="$PROPS -Ploader_version=$LOADER"
        echo "  [DRY-RUN] Would build: $PROPS"
        echo ""
        continue
    fi

    GRADLE_ARGS=()
    $CLEAN && GRADLE_ARGS+=(clean)
    GRADLE_ARGS+=(build)
    $NO_DAEMON && GRADLE_ARGS+=(--no-daemon)
    $WARN && GRADLE_ARGS+=(--warning-mode=none)

    START_TIME=$(date +%s)

    PROP_ARGS=(-DmcVersion="$MC_SRC" -PmcVersion="$MC_SRC" -Pminecraft_version="$MINECRAFT_VER" -Pfabric_api_version="$FABRIC_API")
    [ -n "$YARN" ] && PROP_ARGS+=(-Pyarn_mappings="$YARN")
    [ -n "$LOADER" ] && PROP_ARGS+=(-Ploader_version="$LOADER")

    ./gradlew "${GRADLE_ARGS[@]}" "${PROP_ARGS[@]}" 2>&1 | while IFS= read -r line; do echo "  $line"; done

    EXIT_CODE=${PIPESTATUS[0]}
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))

    if [ $EXIT_CODE -eq 0 ]; then
        JAR_PATH=$(ls build/libs/*.jar 2>/dev/null | grep -v "\-sources" | grep -v "\-dev" | head -n 1)

        if [ -n "$JAR_PATH" ]; then
            INSTANCE_DIR=$(jq -r --arg v "$MC_VERSION" '.instances[$v] // empty' "$CONFIG_FILE")

            if [ -n "$LAUNCHER_DIR" ] && [ "$LAUNCHER_DIR" != "null" ] && [ -n "$INSTANCE_DIR" ]; then
                DEST_DIR="${LAUNCHER_DIR%/}/${INSTANCE_DIR%/}/${LOCATION%/}/"
                mkdir -p "$DEST_DIR"
                cp "$JAR_PATH" "$DEST_DIR"
                echo "  -> Copied to $DEST_DIR"
            else
                echo "  -> JAR: $JAR_PATH (no deploy target configured)"
            fi
        fi
        echo "  [OK] Built in ${DURATION}s"
    else
        echo "  [FAIL] Build failed in ${DURATION}s"
        BUILD_ALL_SUCCESS=false
    fi
    echo ""
done <<< "$VERSIONS"

echo "=========================================="
if $BUILD_ALL_SUCCESS; then
    echo "  All versions built successfully!"
else
    echo "  Some versions failed to build."
    exit 1
fi
echo "=========================================="
