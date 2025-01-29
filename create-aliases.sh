#!/bin/bash

SCRIPT_NAME=$(basename "$0")

COMMANDS_DIR=$(pwd)

echo "🔍 Setting up Git aliases for scripts in: $COMMANDS_DIR"

for script in "$COMMANDS_DIR"/*; do
    if [[ -x "$script" && ! -d "$script" ]]; then
        script_name=$(basename "$script")

        if [[ "$script_name" == "$SCRIPT_NAME" ]]; then
            continue
        fi

        script_path=$(realpath "$script")

        git config --global alias."$script_name" "!bash '$script_path'"

        echo "✅ Created alias: git $script_name -> $script_path"
    fi
done

echo "🎉 All aliases have been set up successfully!"
