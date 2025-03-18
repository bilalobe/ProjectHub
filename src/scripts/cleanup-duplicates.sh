#!/bin/bash

set -euo pipefail

# Base paths
foundation_path="k:/ProjectHub/modules/foundation"
auth_path="k:/ProjectHub/modules/auth"
gateway_path="k:/ProjectHub/modules/gateway"
backup_dir="k:/ProjectHub/backup-duplicates"

# Create backup directory
mkdir -p "$backup_dir"
echo "Created backup directory at $backup_dir"

# Backup and remove function
backup_and_remove_file() {
    local file_path="$1"
    if [ -f "$file_path" ]; then
        # Create relative backup path
        local rel_path="${file_path#$foundation_path}"
        local backup_path="$backup_dir$rel_path"
        local backup_folder="$(dirname "$backup_path")"
        
        mkdir -p "$backup_folder"
        cp "$file_path" "$backup_path"
        echo "Backed up to $backup_path"
        
        rm "$file_path"
        echo "Removed $file_path"
        return 0
    else
        echo "File not found: $file_path"
        return 1
    fi
}

# Arrays of files to clean up
# JWT and auth files
auth_files=(
    "$foundation_path/src/main/java/com/projecthub/base/shared/utils/JwtUtil.java"
    "$foundation_path/src/main/java/com/projecthub/base/shared/exception/BaseException.java"
    # ...existing paths...
)

# Process files
echo "Cleaning up files..."
for file in "${auth_files[@]}"; do
    backup_and_remove_file "$file"
done

# Add all the other arrays and cleanup sections following the same pattern
# ...existing code for other file types...

echo "Cleanup complete! Files have been backed up to $backup_dir"
exit 0
