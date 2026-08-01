#!/bin/bash

COMMANDS_DIR=$(dirname "$(realpath "$0")")

SCRIPT_NAME=$(basename "$0")

echo "🔍 Setting up Git aliases for scripts in: $COMMANDS_DIR"

# Loop through all files in the script's directory
for script in "$COMMANDS_DIR"/*; do
    # Check if the item is an executable file (and not a directory)
    if [[ -x "$script" && ! -d "$script" ]]; then
        script_name=$(basename "$script")

        # Skip the setup script itself
        if [[ "$script_name" == "$SCRIPT_NAME" ]]; then
            continue
        fi

        script_path=$(realpath "$script")

        # The '!' tells Git to treat the command as a shell command.
        git config --global alias."$script_name" "!bash '$script_path'"

        echo "✅ Created alias: git $script_name -> $script_path"
    fi
done

echo "🎉 All aliases have been set up successfully!"
