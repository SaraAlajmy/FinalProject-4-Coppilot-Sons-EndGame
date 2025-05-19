#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# --- Configuration ---
K8S_MANIFEST_DIR="./k8s" # IMPORTANT: Update this if your manifest directory is different
DOCKERHUB_USERNAME="farahalfawzy" # Your Docker Hub username

# --- Script Logic ---

# 1. Generate a unique image tag
RANDOM_TAG="build-$(date +%s)-$(uuidgen | head -c 8 | tr '[:upper:]' '[:lower:]')"
echo "Generated image tag: $RANDOM_TAG"

# Define all available services and their directories
ALL_SERVICES_CONFIG=(
  "chat-service:./chat-service"
  "api-gateway-service:./gateway"
  "group-chat-service:./groupChatService"
  "notification-service:./notification-service"
  "user-service:./userService"
)

SERVICES_TO_BUILD_AND_LOAD=()

if [ "$#" -eq 0 ]; then
  echo "No specific services provided. Processing all services."
  SERVICES_TO_BUILD_AND_LOAD=("${ALL_SERVICES_CONFIG[@]}")
else
  echo "Processing selected services: $@..."
  for requested_service_name in "$@"; do
    found=false
    for service_config_item in "${ALL_SERVICES_CONFIG[@]}"; do
      # Extract the service name part from "service-name:./directory"
      current_service_name_from_config="${service_config_item%%:*}"
      if [ "$current_service_name_from_config" == "$requested_service_name" ]; then
        SERVICES_TO_BUILD_AND_LOAD+=("$service_config_item")
        found=true
        break
      fi
    done
    if [ "$found" == "false" ]; then
      echo "Warning: Service '$requested_service_name' not defined in script's ALL_SERVICES_CONFIG. Skipping."
    fi
  done
fi

if [ ${#SERVICES_TO_BUILD_AND_LOAD[@]} -eq 0 ]; then
  if [ "$#" -gt 0 ]; then
    echo "Error: None of the specified services were found in the script's configuration. No images will be built or loaded. Exiting."
  else
    echo "Error: No services configured in the script. No images will be built or loaded. Exiting." # Should ideally not happen if ALL_SERVICES_CONFIG is populated
  fi
  exit 1
fi

# 2. Build Maven projects (once for all modules)
echo "Building Maven projects..."
mvn clean install -DskipTests
mvn package -DskipTests
echo "Maven projects built."

# 3. Build Docker images for selected services and load into Minikube
echo "Building and loading selected Docker images with tag '$RANDOM_TAG'..."
for service_info in "${SERVICES_TO_BUILD_AND_LOAD[@]}"; do
  IFS=':' read -r service_name service_dir <<< "$service_info"
  
  IMAGE_NAME_WITH_TAG="${DOCKERHUB_USERNAME}/${service_name}:${RANDOM_TAG}"
  
  echo "Building Docker image for $service_name..."
  docker build -t "$IMAGE_NAME_WITH_TAG" "${service_dir}"
  echo "Built $IMAGE_NAME_WITH_TAG"
  
  echo "Loading image $IMAGE_NAME_WITH_TAG into Minikube..."
  minikube image load "$IMAGE_NAME_WITH_TAG"
  echo "Loaded $IMAGE_NAME_WITH_TAG into Minikube."
done
echo "Selected Docker images built and loaded into Minikube."

# 4. Deploy to Kubernetes
echo "Preparing to apply Kubernetes manifests from '$K8S_MANIFEST_DIR'..."
echo "Image tags ':latest' for images under '${DOCKERHUB_USERNAME}/' will be temporarily replaced with ':$RANDOM_TAG' for deployment."
echo "Original manifest files will NOT be modified."

# Check if the manifest directory exists
if [ ! -d "$K8S_MANIFEST_DIR" ]; then
  echo "Error: Kubernetes manifest directory '$K8S_MANIFEST_DIR' not found."
  echo "Please create this directory and add your Kubernetes YAML files to it,"
  echo "or update the K8S_MANIFEST_DIR variable in this script."
  exit 1
fi

# Process all YAML/YML files and apply them
# The subshell (...) groups the output of the find loop.
(
  find "$K8S_MANIFEST_DIR" -type f \( -name "*.yaml" -o -name "*.yml" \) -print0 | while IFS= read -r -d $'\0' K8S_FILE; do
    echo "---" # YAML document separator
    # Perform tag replacement and enforce imagePullPolicy: Never on our images
    # This sed command looks for 'image: DOCKERHUB_USERNAME/image-name:latest'
    # and replaces ':latest' with ':<RANDOM_TAG>'.
    # It uses extended regex (-E) and | as a delimiter for s command.
    sed -E \
      -e "s|(image:[[:space:]]*${DOCKERHUB_USERNAME}/[^:]+):latest|\\1:${RANDOM_TAG}|g" \
      -e "/image:[[:space:]]*${DOCKERHUB_USERNAME}\\/[^:]+/a\\
          imagePullPolicy: Never" \
      "$K8S_FILE"
    echo # Add a newline for readability of the piped stream
  done
) | kubectl apply -f -

# Check the exit status of kubectl apply
if [ $? -eq 0 ]; then
  echo ""
  echo "Kubernetes manifests applied successfully using image tag ':$RANDOM_TAG' for relevant images."
else
  echo ""
  echo "Error: kubectl apply failed. Please check the output above, your kubectl configuration, and the manifest files."
  exit 1
fi

echo "Script finished."
