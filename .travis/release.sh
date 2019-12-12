#!/bin/bash
if [ -n "$TRAVIS_TAG" ]; then
  docker pull registry.access.redhat.com/fuse7/fuse-java-openshift
  
  # Build image
  echo "Docker build empezo"
  mvn fabric8:build -Dfabric8.mode=kubernetes
  echo "Docker build termino"

  docker images

  # Tag Docker image
  docker tag xavier/xavier-integration xavier/xavier-integration:latest
  docker tag xavier/xavier-integration xavier/xavier-integration:"$TRAVIS_TAG"

  # Push our image to the Image Registry
  docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD";
  docker push xavier/xavier-integration
fi
