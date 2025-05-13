#!/bin/bash

# Define the RabbitMQ configuration directory
CONFIG_FILE="/etc/rabbitmq/rabbitmq.conf"

# Backup existing configuration if needed
if [ -f "$CONFIG_FILE" ]; then
  cp "$CONFIG_FILE" "${CONFIG_FILE}.bak"
fi

# Dynamically generate or modify the configuration file
cat <<EOF > "$CONFIG_FILE"
# RabbitMQ Configuration File
definitions.import_backend = local_filesystem
definitions.local.path = /etc/rabbitmq/definitions.json
listeners.tcp.default = 5672
management.tcp.port = 15672

# Example dynamic value
log.file.level = ${LOG_LEVEL:-info}
EOF

# Add additional settings dynamically if needed
echo "cluster_formation.peer_discovery_backend = classic_config" >> "$CONFIG_FILE"

# Ensure the correct permissions
chmod 644 "$CONFIG_FILE"

# Start RabbitMQ
exec docker-entrypoint.sh rabbitmq-server