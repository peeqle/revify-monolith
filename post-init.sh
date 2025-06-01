#!/bin/bash

./docker-entrypoint.sh "$@" &
wait

rabbitmq-plugins enable rabbitmq_stomp
rabbitmq-plugins enable rabbitmq_management
rabbitmq-plugins enable rabbitmq_federation
rabbitmq-plugins enable rabbitmq_federation_management
rabbitmq-plugins enable rabbitmq_delayed_message_exchange

wait
