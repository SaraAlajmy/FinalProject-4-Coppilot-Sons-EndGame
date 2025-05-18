kubectl apply -R -f ./k8s/

cd e2e-tests

docker compose up -d mailhog

kubectl apply -f ./e2e-k8s

mvn clean install -DskipTests

sleep 5

BASE_URL=http://$(minikube ip):30080 mvn verify

docker compose down

cd ../

kubectl apply -R -f ./k8s/