#!/bin/bash
if [ -n "$TRAVIS_TAG" ]; then
  docker pull registry.access.redhat.com/fuse7/fuse-java-openshift
  docker tag registry.access.redhat.com/fuse7/fuse-java-openshift:latest fuse7/fuse-java-openshift:latest
  docker tag registry.access.redhat.com/fuse7/fuse-java-openshift:latest fuse7/fuse-java-openshift:1.3

  # Build image
  mvn fabric8:build -Dfabric8.mode=kubernetes

  docker images

  # Tag Docker image
  docker tag xavier/xavier-integration:"$TRAVIS_TAG" projectxavier/xavier-integration:latest
  docker tag xavier/xavier-integration:"$TRAVIS_TAG" projectxavier/xavier-integration:"$TRAVIS_TAG"

  # Docker Hub login
  docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD";

  # Docker push
  docker push projectxavier/xavier-integration:latest
  docker push projectxavier/xavier-integration:"$TRAVIS_TAG"
fi
