#!/bin/bash
if [ -n "$TRAVIS_TAG" ]; then
  # We can choose eithe follow this block of commands to download the image without authentication in Red Hat Registry
  # Or we can create a Registry Service Account and login here
  docker pull registry.access.redhat.com/fuse7/fuse-java-openshift
  docker tag registry.access.redhat.com/fuse7/fuse-java-openshift:latest fuse7/fuse-java-openshift:latest
  docker tag registry.access.redhat.com/fuse7/fuse-java-openshift:latest fuse7/fuse-java-openshift:1.3 # fabric8 use this specific version

  # Build image
  mvn fabric8:build -Dfabric8.mode=kubernetes

  # Tag Docker image
  docker tag xavier/xavier-integration:"$TRAVIS_TAG" projectxavier/xavier-integration:latest
  docker tag xavier/xavier-integration:"$TRAVIS_TAG" projectxavier/xavier-integration:"$TRAVIS_TAG"

  # Docker Hub login
  docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD";

  # Docker push
  docker push projectxavier/xavier-integration:latest
  docker push projectxavier/xavier-integration:"$TRAVIS_TAG"
fi
