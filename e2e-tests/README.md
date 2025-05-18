# End-to-End Tests

## Run against compose cluster

Use the following command in root directory to run the end-to-end tests against a compose cluster:

```bash
./test.sh
```

## Run against a minikube cluster

Make sure you have a minikube cluster running. You can use the following command to start a minikube cluster:

```bash
minikube start
```

Then, you can run the end-to-end tests against the minikube cluster using the following command:

```bash
./k8s-test.sh
```
