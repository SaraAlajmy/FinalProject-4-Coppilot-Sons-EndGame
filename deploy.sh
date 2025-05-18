#!/bin/bash

# --- Configuration ---
# The old username to be replaced in the Kubernetes manifests
OLD_USERNAME="farahalfawzy"
# The new username to use (your GitHub Container Registry username)
NEW_USERNAME="copilotandsons"
# The directory containing your Kubernetes manifest files (.yaml or .yml)
K8S_MANIFEST_DIR="./k8s" # IMPORTANT: Update this to your actual manifest directory path

# --- Script Logic ---

# Check for PR number argument
if [ -z "$1" ]; then
  echo "Error: PR number argument is missing."
  echo "Usage: $0 <pr_number>"
  exit 1
fi
PR_NUMBER="pr-$1"

# Check if the manifest directory exists
if [ ! -d "$K8S_MANIFEST_DIR" ]; then
  echo "Error: Kubernetes manifest directory '$K8S_MANIFEST_DIR' not found."
  echo "Please create this directory and add your Kubernetes YAML files to it,"
  echo "or update the K8S_MANIFEST_DIR variable in this script."
  exit 1
fi

echo "Preparing to apply Kubernetes manifests from '$K8S_MANIFEST_DIR'..."
echo "Image usernames will be temporarily updated from '$OLD_USERNAME' to '$NEW_USERNAME'."
echo "Image tags will be temporarily updated from ':latest' to ':$PR_NUMBER'."
echo "Original manifest files will NOT be modified."

# Process all YAML/YML files:
# 1. Find all matching files.
# 2. For each file, use sed to replace the username and tag, then output to stdout.
# 3. Prepend "---" to ensure proper YAML document separation for kubectl.
# 4. Pipe the combined stream of modified manifests to kubectl apply.
# The subshell (...) groups the output of the find loop.
(
  find "$K8S_MANIFEST_DIR" -type f \( -name "*.yaml" -o -name "*.yml" \) -print0 | while IFS= read -r -d $'\0' K8S_FILE; do
    echo "---" # YAML document separator
    # Perform replacements and output to stdout.
    # Using '|' as a delimiter for sed in case paths/usernames contain '/'
    # Chain sed commands:
    # 1. Replace OLD_USERNAME with NEW_USERNAME globally.
    # 2. Replace ':latest' with ':$PR_NUMBER' ONLY for images under NEW_USERNAME.
    sed -e "s|$OLD_USERNAME|$NEW_USERNAME|g" \
        -e "s|\($NEW_USERNAME/[^:]\{1,\}\):latest|\1:$PR_NUMBER|g" "$K8S_FILE"
    echo
  done
) | kubectl apply -f -

# Check the exit status of kubectl apply
if [ $? -eq 0 ]; then
  echo ""
  echo "Kubernetes manifests applied successfully using the modified stream with image tag ':$PR_NUMBER'."
else
  echo ""
  echo "Error: kubectl apply failed. Please check the output above, your kubectl configuration, and the manifest files."
  exit 1
fi
