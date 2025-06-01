FROM rabbitmq:4-management

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

ARG RABBITMQ_VERSION=4.1.0
RUN curl -L https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v${RABBITMQ_VERSION}/rabbitmq_delayed_message_exchange-${RABBITMQ_VERSION}.ez > /opt/rabbitmq/plugins/rabbitmq_delayed_message_exchange-${RABBITMQ_VERSION}.ez

RUN chown rabbitmq:rabbitmq /opt/rabbitmq/plugins/rabbitmq_delayed_message_exchange-${RABBITMQ_VERSION}.ez

RUN echo '[rabbitmq_management,rabbitmq_delayed_message_exchange].' > /etc/rabbitmq/enabled_plugins