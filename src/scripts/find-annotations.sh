#!/bin/bash

project_root="K:/ProjectHub"

echo "Finding unique annotations in alphabetical order:"
find "$project_root" -type f -name "*.java" -exec grep -ho '@\w\+\([^)]*\)*' {} \; | 
    sed 's/^@//' |
    sed 's/(.*)$//' |
    sort -u

if [ $? -ne 0 ]; then
    echo "Error occurred while searching files" >&2
    echo "Current location: $(pwd)" >&2
    echo "Make sure path '$project_root' exists and is accessible" >&2
    exit 1
fi
