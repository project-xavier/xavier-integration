#!/usr/bin/env bash

echo "===================================="
read -p "Enter Release: " releaseVersion
read -p "Enter Next Release (SNAPSHOT): " nextVersion
read -p "Enter Github Username: " username
read -s -p "Enter Github Password: " password

# Update to release
mvn versions:set -DnewVersion="$releaseVersion"

git add .
git commit -m "Release $releaseVersion"
git push "https://$username:$password@github.com/project-xavier/xavier-integration.git"

# Create tag and push
git tag "$releaseVersion"
git push "https://$username:$password@github.com/project-xavier/xavier-integration.git" --tags

# Create next snapshot
mvn versions:set -DnewVersion="$nextVersion-SNAPSHOT"

git add .
git commit -m "Prepare next release $nextVersion-SNAPSHOT"
git push "https://$username:$password@github.com/project-xavier/xavier-integration.git"
