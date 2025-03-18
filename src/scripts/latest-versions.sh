#!/usr/bin/env bash
#
# Usage:
#   1) Make sure you have "curl" and "jq" installed.
#   2) Save this script as, for example, "latest-versions.sh" and make it executable:
#       chmod +x latest-versions.sh
#   3) Run it:
#       ./latest-versions.sh

# Array of "groupId:artifactId" entries you want to check
declare -a artifacts=(
  "org.springframework.boot:spring-boot-gradle-plugin"
  "org.asciidoctor:asciidoctor-gradle-jvm"
  "org.asciidoctor:asciidoctorj-pdf"
  "org.asciidoctor:asciidoctorj-diagram"
  "com.yubico:webauthn-server-core"
  "com.yubico:webauthn-server-attestation"
  "org.apache.directory.fortress:fortress-core"
  "org.apache.directory.fortress:fortress-realm-impl"
  "org.mapstruct:mapstruct"
  "org.projectlombok:lombok"
  "org.springframework.boot:spring-boot-starter-web"
  "org.springframework.boot:spring-boot-starter-security"
  "org.springframework.boot:spring-boot-starter-data-jpa"
  "org.springframework.boot:spring-boot-starter-data-rest"
  "org.springframework.boot:spring-boot-starter-thymeleaf"
  "org.springframework.boot:spring-boot-starter-test"
  "org.springframework.boot:spring-boot-starter-actuator")

echo "Fetching latest metadata from Maven Central..."
echo

for artifact in "${artifacts[@]}"; do
  # Split string by colon to extract groupId and artifactId
  IFS=":" read -r groupId artifactId <<< "${artifact}"

  # Escape dots in groupId for the search query
  groupIdEscaped="${groupId//./\\.}"

  # Build the search URL. 
  #  - q uses the Lucene query syntax: g:"groupId" AND a:"artifactId"
  #  - sort=version desc picks the highest version first
  url="https://search.maven.org/solrsearch/select?q=g:\"${groupIdEscaped}\"+AND+a:\"${artifactId}\"&core=gav&rows=1&wt=json&sort=version+desc"

  # Make the request and parse JSON using jq
  response=$(curl -s "${url}")
  latestVersion=$(echo "${response}" | jq -r '.response.docs[0].v // empty')

  if [[ -n "${latestVersion}" ]]; then
    echo "${artifact} => Latest version: ${latestVersion}"
  else
    echo "${artifact} => No version found (check artifact coordinates or connectivity)"
  fi
done

echo
echo "Done."
