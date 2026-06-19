#!/bin/bash

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
    VERSION=$(echo "$version_data" | jq -r '.version')
    MC_SRC=$(echo "$version_data" | jq -r '.mcVersion // .version')
    MINECRAFT_VER=$(echo "$version_data" | jq -r '.minecraft_version')
    YARN=$(echo "$version_data" | jq -r '.yarn_mappings')
    FABRIC_API=$(echo "$version_data" | jq -r '.fabric_api_version')

    echo "--- Building for Minecraft $VERSION ($MINECRAFT_VER) ---"

    if [ "$DRY_RUN" = true ]; then
        echo "  [DRY-RUN] Would build: -PmcVersion=$MC_SRC -Pminecraft_version=$MINECRAFT_VER -Pyarn_mappings=$YARN -Pfabric_api_version=$FABRIC_API"
        echo ""
        continue
    fi

    GRADLE_ARGS=()
    $CLEAN && GRADLE_ARGS+=(clean)
    GRADLE_ARGS+=(build)
    $NO_DAEMON && GRADLE_ARGS+=(--no-daemon)
    $WARN && GRADLE_ARGS+=(--warning-mode=none)

    START_TIME=$(date +%s)
    ./gradlew "${GRADLE_ARGS[@]}" \
        -PmcVersion="$MC_SRC" \
        -Pminecraft_version="$MINECRAFT_VER" \
        -Pyarn_mappings="$YARN" \
        -Pfabric_api_version="$FABRIC_API" \
        2>&1 | while IFS= read -r line; do echo "  $line"; done

    EXIT_CODE=${PIPESTATUS[0]}
    END_TIME=$(date +%s)
    DURATION=$((END_TIME - START_TIME))

    if [ $EXIT_CODE -eq 0 ]; then
        JAR_PATH=$(ls build/libs/*.jar 2>/dev/null | grep -v "\-sources" | grep -v "\-dev" | head -n 1)

        if [ -n "$JAR_PATH" ]; then
            INSTANCE_DIR=$(echo "$version_data" | jq -r '.instanceDir // empty')
            if [ -z "$INSTANCE_DIR" ] || [ "$INSTANCE_DIR" = "null" ]; then
                INSTANCE_DIR=$(jq -r --arg v "$VERSION" '.instances[$v] // empty' "$CONFIG_FILE")
            fi

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
