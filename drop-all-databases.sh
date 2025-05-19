#!/usr/bin/env bash
# fail fast
set -euo pipefail

# Configuration: adjust these label selectors to match your k8s setup
postgres_label="app=postgres"
mongo_label="app=mongo"

# Credentials (via env vars)
pg_user="your_pg_username"
pg_password="your_pg_password"
mongo_user="root"
mongo_password="example"
mongo_auth_db="admin"
redis_label="app=redis"
redis_password="${REDIS_PASSWORD:-}"

# Function to drop all Postgres databases except system DBs
drop_postgres_dbs() {
  pod=$(kubectl get pod -l "$postgres_label" -o jsonpath="{.items[0].metadata.name}")
  echo "Dropping all Postgres databases in pod $pod..."

  # List non-template DBs
  dbs=$(kubectl exec "$pod" -- env PGPASSWORD="$pg_password" psql -U "$pg_user" -d postgres -Atc "SELECT datname FROM pg_database WHERE datistemplate = false;")
  for db in $dbs; do
    if [[ "$db" != "postgres" ]]; then
      echo "  Dropping database: $db"
      kubectl exec "$pod" -- env PGPASSWORD="$pg_password" psql -U "$pg_user" -d postgres -c "DROP DATABASE IF EXISTS \"$db\" WITH (FORCE);"
      echo "  Recreating database: $db"
      kubectl exec "$pod" -- env PGPASSWORD="$pg_password" psql -U "$pg_user" -d postgres -c "CREATE DATABASE \"$db\";"
    fi
  done
  echo "Postgres cleanup complete."  
}

# Function to drop all MongoDB databases except system DBs
drop_mongo_dbs() {
  pod=$(kubectl get pod -l "$mongo_label" -o jsonpath="{.items[0].metadata.name}")
  echo "Dropping all MongoDB databases in pod $pod..."

  uri="mongodb://$mongo_user:$mongo_password@127.0.0.1:27017/$mongo_auth_db?authSource=$mongo_auth_db"
  kubectl exec "$pod" -- mongosh "$uri" --quiet --eval '
    // list all databases via adminCommand
    const dbs = db.adminCommand({ listDatabases: 1 }).databases;
    dbs.forEach(d => {
      if (!["admin", "local", "config"].includes(d.name)) {
        const target = db.getSiblingDB(d.name);
        target.dropDatabase();
        print("  Dropped MongoDB: " + d.name);
      }
    });'
  echo "MongoDB cleanup complete."
}

# Function to clear Redis cache
drop_redis_cache() {
  pod=$(kubectl get pod -l "$redis_label" -o jsonpath="{.items[0].metadata.name}")
  echo "Clearing Redis cache in pod $pod..."
  if [[ -n "$redis_password" ]]; then
    kubectl exec "$pod" -- redis-cli -a "$redis_password" FLUSHALL
  else
    kubectl exec "$pod" -- redis-cli FLUSHALL
  fi
  echo "Redis cache cleared."
}

# Execute both
drop_postgres_dbs
drop_mongo_dbs
drop_redis_cache

# delete pods
./delete-microservice-pods.sh
