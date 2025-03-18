#!/bin/bash

# Create domain layer
mkdir -p src/main/kotlin/com/projecthub/domain/{project,milestone,user}

# Create application layer
mkdir -p src/main/kotlin/com/projecthub/application/{project,auth}

# Create infrastructure layer
mkdir -p src/main/kotlin/com/projecthub/infrastructure/{ktor/{plugins,routes},rabbitmq,mongodb,firebase}

# Create api layer
mkdir -p src/main/kotlin/com/projecthub/api/{rest,graphql,websocket}
