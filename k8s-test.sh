#!/bin/bash

PR_NUMBER=$1

if [ -n "$PR_NUMBER" ]; then
  ./deploy.sh "$PR_NUMBER"
else
  kubectl apply -R -f ./k8s/
fi

cd e2e-tests

docker compose up -d mailhog

kubectl apply -f ./e2e-k8s

mvn clean install -DskipTests

sleep 5

BASE_URL=http://$(minikube ip):30080 mvn verify

docker compose down

cd ../

if [ -n "$PR_NUMBER" ]; then
  ./deploy.sh "$PR_NUMBER"
else
  kubectl apply -R -f ./k8s/
fi
