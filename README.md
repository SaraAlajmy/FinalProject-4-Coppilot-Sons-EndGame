1| # ACL Final Project
2| <!-- GitHub Copilot: This is a microservices architecture project with multiple services including chat, user, and notification services built with Spring Boot -->
3| 
4| ## Overview
5| 
6| The project consists of several services, including a chat service, user service, notification service, group chat service, and an API gateway. It utilizes technologies like Java (Spring Boot likel[...]
7| 
8| ## Prerequisites
9| 
10| Before you begin, ensure you have the following installed:
11| *   Java (JDK)
12| *   Maven
13| *   Docker & Docker Compose
14| *   kubectl
15| *   Minikube (for Kubernetes deployment)
16| 
17| ## Getting Started
18| 
19| ### 1. Clone the Repository
20| 
21| ```bash
22| git clone <your-repository-url>
23| ```
24| 
25| ### 2. Build the Application
26| 
27| All services are Maven-based. To build all services and create the necessary JAR files:
28| 
29| ```bash
30| mvn clean install -DskipTests
31| mvn package -DskipTests
32| ```
33| This command should be run at the root of the project where the main `pom.xml` is located, which will then build the sub-modules (services).
34| 
35| ## Running the Project
36| 
37| You have two main options for running the application:
38| 
39| ### Option 1: Using Docker Compose (for local development)
40| 
41| This method uses Docker Compose to build images for each service and run them along with backing services like MongoDB and RabbitMQ as defined in `docker-compose.yml` and service-specific compose [...]
42| 
43| 1.  **Start the application:**
44|     ```bash
45|     ./start.sh
46|     ```
47|     This script builds the JARs and starts the application using Docker Compose. It uses the `docker-compose.yml` file located in the E2E tests directory.
48|     
49| > **Note:** This will use MailHog for email testing instead of GMail. If you want to use GMail, you will need to override SPRING_MAIL properties in the `docker-compose.yml` file or with environmen[...]
50| 
51| ### Option 2: Using Kubernetes (Minikube for local cluster)
52| 
53| Make sure you have Minikube running. You can start it with:
54| 
55| ```bash
56| minikube start
57| ```
58| 
59| Apply the Kubernetes manifests to your Minikube cluster:
60| 
61| ```bash
62| kubectl apply -f k8s/
63| ```
64| 
65| Get access to the gateway service:
66| 
67| ```bash
68| minikube service apigateway-service --url
69| ```
70| 
71| ## Running Tests
72| 
73| ### Local/Docker Compose Tests
74| 
75| The `test.sh` script is provided for running tests, end-to-end tests using the Docker Compose setup.
76| 
77| ```bash
78| ./test.sh
79| ```
80| 
81| ### Kubernetes Tests
82| 
83| The `k8s-test.sh` script is designed to run tests against a Kubernetes deployment.
84| 
85| ```bash
86| ./k8s-test.sh
87| ```
88| 
89| >**Note:** This script requires a running Kubernetes cluster using Minikube. It will also start a `mailhog` container for email testing.
90| 
91| >**Note:** This runs against current manifests in `k8s/` and use `mailhog` for email testing (by applying the manifests in `e2e-tests/e2e-k8s/`), to run tests against a specific PR, use `./k8s-tes[...]
92| 
93| This script:
94| 1.  Applies Kubernetes manifests from `./k8s/`.
95| 3.  Starts `mailhog` using Docker Compose.
96| 4.  Applies Kubernetes manifests from `./e2e-tests/e2e-k8s/` to use the `mailhog` service instead of GMail
97| 5.  Builds the `e2e-tests` module.
98| 7.  Runs `mvn verify` with a `BASE_URL` pointing to the `api-gateway` service in the Kubernetes cluster.
99| 8.  Stops the `mailhog` container.
100| 9.  Re-applies the main Kubernetes manifests.
101| 
102| ## Deployment Script (`deploy.sh`)
103| 
104| The `deploy.sh` script facilitates deployment to Kubernetes.
105| *   It takes a Pull Request number as an argument, which is used to tag Docker images (e.g., `pr-123`).
106| *   It applies these manifests to your Kubernetes cluster using `kubectl apply -f -` using images built from the PR.
107| 
108| **Usage:**
109| ```bash
110| ./deploy.sh <pr_number>
111| ```
112| 
113| ## Services
114| 
115| The project includes the following services:
116| 
117| *   **`api-gateway`**: (Located in `gateway/`) Entry point for client requests.
118| *   **`chat-service`**: (Located in `chat-service/`) Handles chat functionalities.
119| *   **`group-chat-service`**: (Located in `groupChatService/`) Manages group chat features.
120| *   **`notification-service`**: (Located in `notification-service/`) Responsible for sending notifications.
121| *   **`user-service`**: (Located in `userService/`) Manages user accounts and authentication.
122| *   **`e2e-tests`**: (Located in `e2e-tests/`) Contains end-to-end tests for the application.
123| 
124| **Datastores & Message Brokers:**
125| *   **MongoDB**: NoSQL database.
126| *   **RabbitMQ**: Message broker.
127| *   **PostgreSQL**: SQL database.
128| *   **Redis**: In-memory data store.
129| 
130| ## Kubernetes Manifests
131| 
132| Kubernetes manifest files are located in the `k8s/` directory, with subdirectories for each service and component.
133| Additional test-specific Kubernetes manifests can be found in `e2e-tests/e2e-k8s/`.
