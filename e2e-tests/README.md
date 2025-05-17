# End-to-End Tests

## Run against compose cluster

1. Build the jars

```bash
mvn clean install -DskipTests
mvn package -DskipTests
```

2. Make sure you are in the `e2e-tests` directory.
3. Run the following command to start the environment:

```bash
docker-compose up -d
```

4. Wait for the containers to be up and running.

5. Run the tests using the following command:

```bash
mvn install
mvn verify
```