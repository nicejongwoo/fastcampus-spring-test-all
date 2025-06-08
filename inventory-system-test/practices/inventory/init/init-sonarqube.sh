#!/bin/bash

NEW_PASSWORD="!@passwordA123"
TOKEN_NAME="my-local-token"
ENV_FILE="/workspace/.env"

echo "[INFO] Waiting for SonarQube to become healthy..."

until curl -s http://sonarqube:9000/api/system/status | grep -q '"status":"UP"'; do
  echo "[INFO] Waiting..."
  sleep 3
done

echo "[INFO] Changing admin password..."
curl -s -u admin:admin -X POST "http://sonarqube:9000/api/users/change_password" \
  -d "login=admin&previousPassword=admin&password=$NEW_PASSWORD"

echo "[INFO] Generating token..."
RESPONSE=$(curl -s -u admin:$NEW_PASSWORD -X POST "http://sonarqube:9000/api/user_tokens/generate" \
  -d "name=$TOKEN_NAME")

TOKEN=$(echo "$RESPONSE" | grep -oP '"token":"\K[^"]+')

echo "[INFO] Token: $TOKEN"

if [[ -n "$TOKEN" ]]; then
  echo "SONAR_TOKEN=$TOKEN" > "$ENV_FILE"
  echo "[INFO] Saved SONAR_TOKEN to $ENV_FILE"
else
  echo "[ERROR] Failed to extract token"
  exit 1
fi