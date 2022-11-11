#!/bin/bash
find ./src/main/ -name '*.kt' -print0 |
while IFS= read -r -d '' sourceFile; do
    fileContent=$(cat "$sourceFile")
    if ! [[ "$fileContent" =~ (MIT License) ]]; then
          printf '%s\n' "[Missing/Wrong License] in $sourceFile" >&2
          exit 1
    fi
done
