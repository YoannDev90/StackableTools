#!/bin/bash

# 🔍 Script pour trouver automatiquement le dossier des launchers Minecraft (Prism, Freesm, MultiMC, etc.)

echo "🔎 Recherche des launchers Minecraft installés..."

# Liste des chemins communs par OS (Fallback si les commandes échouent)
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Essayer de détecter via Flatpak si disponible
    if command -v flatpak >/dev/null 2>&1; then
        echo "📦 Détection via Flatpak..."
        FLATPAK_APPS=$(flatpak list --app --columns=application)
        
        # Prism
        if echo "$FLATPAK_APPS" | grep -q "org.prismlauncher.PrismLauncher"; then
            SEARCH_PATHS+=("$HOME/.var/app/org.prismlauncher.PrismLauncher/data/PrismLauncher")
        fi
        # Freesm
        if echo "$FLATPAK_APPS" | grep -q -E "org.freesmTeam.freesmlauncher|io.github.freesm.FreesmLauncher"; then
            SEARCH_PATHS+=("$HOME/.var/app/org.freesmTeam.freesmlauncher/data/FreesmLauncher" "$HOME/.var/app/io.github.freesm.FreesmLauncher/data/FreesmLauncher")
        fi
        # Fjord
        if echo "$FLATPAK_APPS" | grep -q "org.unmojang.FjordLauncher"; then
            SEARCH_PATHS+=("$HOME/.var/app/org.unmojang.FjordLauncher/data/FjordLauncher")
        fi
    fi

    # Ajout des chemins standards
    SEARCH_PATHS+=(
        "$HOME/.local/share/PrismLauncher"
        "$HOME/.local/share/FreesmLauncher"
        "$HOME/.local/share/FjordLauncher"
        "$HOME/.local/share/MultiMC"
        "$HOME/.minecraft"
        "$HOME/.local/share/PolyMC"
    )
    INSTANCE_SUBDIR="instances"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS - On peut utiliser 'mdfind' pour chercher les Apps installées
    echo "🍎 Détection macOS via mdfind..."
    MDFIND_PRISM=$(mdfind "kMDItemCFBundleIdentifier == 'org.prismlauncher.PrismLauncher'" | head -n 1)
    if [ -n "$MDFIND_PRISM" ]; then
        SEARCH_PATHS+=("$HOME/Library/Application Support/PrismLauncher")
    fi
    
    SEARCH_PATHS+=(
        "$HOME/Library/Application Support/PrismLauncher"
        "$HOME/Library/Application Support/FreesmLauncher"
        "$HOME/Library/Application Support/minecraft"
        "$HOME/Library/Application Support/MultiMC"
    )
    INSTANCE_SUBDIR="instances"
elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]] || [[ "$OSTYPE" == "win32" ]]; then
    # Windows
    SEARCH_PATHS+=(
        "$APPDATA/PrismLauncher"
        "$APPDATA/FreesmLauncher"
        "$APPDATA/MultiMC"
        "$APPDATA/.minecraft"
    )
    INSTANCE_SUBDIR="instances"
fi

FOUND=false

echo "--- Résultats ---"
for path in "${SEARCH_PATHS[@]}"; do
    if [ -d "$path" ]; then
        echo "✅ TROUVÉ : $path"
        
        # Gestion spécifique pour le launcher officiel (versions/ au lieu de instances/)
        if [[ "$path" == *".minecraft"* ]]; then
             if [ -d "$path/versions" ]; then
                echo "   📂 Versions trouvées dans : $path/versions"
                ls -1 "$path/versions" | sed 's/^/      - /'
            fi
        # Pour les launchers type Prism/MultiMC
        elif [ -d "$path/$INSTANCE_SUBDIR" ]; then
            echo "   📂 Instances trouvées dans : $path/$INSTANCE_SUBDIR"
            ls -1 "$path/$INSTANCE_SUBDIR" | sed 's/^/      - /'
        fi
        FOUND=true
    fi
done

if [ "$FOUND" = false ]; then
    echo "❌ Aucun launcher standard n'a été trouvé automatiquement."
    echo "💡 Vérifie ton installation ou ajoute ton chemin personnalisé dans SEARCH_PATHS."
else
    echo "---"
    echo "💡 Copie l'un de ces chemins dans ton fichier compile_config.json (champ output_path)."
fi
