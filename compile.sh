#!/bin/bash
clear

# Définir le répertoire de destination
# Lire les paths depuis le fichier de configuration
CONFIG_FILE="compile_config.json"
if [ -f "$CONFIG_FILE" ]; then
    LAUNCHER_DIR=$(jq -r '.launcherDir' "$CONFIG_FILE")
    INSTANCE_DIR=$(jq -r '.instanceDir' "$CONFIG_FILE") # LAUNCHER_DIR + instanceDir
    LOCATION=$(jq -r '.location' "$CONFIG_FILE") # INSTANCE_DIR + location
else
    echo "Erreur : Le fichier de configuration $CONFIG_FILE est introuvable."
    exit 1
fi

DEST_DIR="${LAUNCHER_DIR%/}/${INSTANCE_DIR%/}/${LOCATION%/}/"

# Vérifier si le répertoire de destination existe
if [ ! -d "$DEST_DIR" ]; then
    if mkdir -p "$DEST_DIR"; then
        echo "Le répertoire de destination a été créé avec succès."
    else
        echo "Erreur : Impossible de créer le répertoire de destination $DEST_DIR."
        exit 1
    fi
fi

echo "Compilation du projet... $(date +"%T")"

# Exécuter le build Gradle
if ./gradlew build; then
    echo "Compilation terminée avec succès ! $(date +"%T")"
    
    # Vérifier si le fichier JAR a été généré
    if [ -f "build/libs/stackabletoolskotlin-1.0.0.jar" ]; then
        # Supprimer le fichier existant s'il existe
        if [ -f "$DEST_DIR/stackabletoolskotlin-1.0.0.jar" ]; then
            rm "$DEST_DIR/stackabletoolskotlin-1.0.0.jar"
            echo "Ancien fichier supprimé."
        fi
        
        # Déplacer le nouveau fichier
        mv "build/libs/stackabletoolskotlin-1.0.0.jar" "$DEST_DIR"
        echo "Fichier JAR déplacé vers la destination."
    else
        echo "Erreur : Le fichier JAR n'a pas été généré."
        exit 1
    fi
else
    echo "Erreur : La compilation a échoué."
    exit 1
fi