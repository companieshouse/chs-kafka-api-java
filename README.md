# chs-kafka-api-java

This microservice transforms a JSON received via an internal HTTP request into a Kafka message. Each entrypoint is
statically configured to be mapped onto its own Kafka topic. It can be easily extended to manage further
entrypoints/Kafka topics.

Context of this
microservice: [Streaming Platform HLD](https://companieshouse.atlassian.net/wiki/spaces/Arch/pages/198934840/Streaming+Platform)

---

## Architecture

The service is organized using a package-by-feature structure, where feature-specific code resides in its own package,
while common functionality is placed in a shared common package.

### Key Components

1. **Kafka Producer**
    - Publishes messages to Kafka topics.
    - Configured per feature/topic in the corresponding package.

2. **Endpoint**
    - Handles HTTP requests for each feature.
    - Validates incoming requests, delegates the work to services.

3. **Services**
    - Orchestrate the mapping and publishing process.
    - Encapsulate business logic for each feature.

4. **Mappers**
    - Convert API request models to Avro/Kafka models.
    - Handle serialization and field mapping, including error handling for invalid payloads.

5. **Common Utilities**
    - Shared code for Kafka configuration, logging, and error handling.
    - Includes base interfaces (e.g., Controller, Service, Mapper), logging filters, and utility classes.

---

## Sequence Diagram

The following sequence diagram illustrates the generic endpoint flow:

[!Sequence Diagram](docs/sequence.png)

---

## End Point Flow

### Filing Processed Feature Flow

- **FilingProcessedController**
    - **Responsibility:** Handles HTTP POST requests for processed filings.
    - **Input:** Receives a `ProcessedFiling` API model (Java POJO) as the request body.
    - **Action:** Validates the request and delegates processing to the service layer.

- **FilingProcessedService**
    - **Responsibility:** Orchestrates the mapping and publishing of processed filing data.
    - **Action:** Calls the mapper to convert the API model to the Avro model, then publishes it to Kafka using the
      producer.

- **FilingProcessedMapper**
    - **Responsibility:** Converts the `ProcessedFiling` API model to the Avro model `filing_processed` (from the Avro
      schema).
    - **Action:** Handles field mapping, including any necessary transformations (e.g., date/time formatting, null
      handling).

- **FilingProcessedKafkaConfig**
    - **Responsibility:** Configures the Kafka producer beans for the filing processed topic.
    - **Action:** Sets up the producer factory, Kafka template, and producer for the `filing_processed` Avro type.

- **LocalDateTimeSupplier**
    - **Responsibility:** Supplies the current `LocalDateTime` when needed (e.g., for timestamp fields in the Avro
      model).
    - **Usage:** Used by the mapper or service to populate date/time fields in the outgoing Kafka message.

### Message Send Feature Flow

- **MessageSendController**
    - **Responsibility:** Handles HTTP POST requests for sending messages.
    - **Input:** Receives a `MessageSend` API model (Java POJO) as the request body.
    - **Action:** Validates the request and delegates processing to the service layer.

- **MessageSendService**
    - **Responsibility:** Orchestrates the mapping and publishing of message send .
    - **Action:** Calls the mapper to convert the API model to the Avro model, then publishes it to Kafka using the
      producer.

- **MessageSendMapper**
    - **Responsibility:** Converts the `MessageSend` API model to the Avro model message_send (as defined in the Avro
      schema).
    - **Action:** Handles field mapping, including any necessary transformations (e.g., serializing nested data fields).

- **MessageSendKafkaConfig**
    - **Responsibility:** Configures the Kafka producer beans for the message send topic.
    - **Action:** Sets up the producer factory, Kafka template, and producer for the message send Avro type.

---

### Error Handling

Error handling in this service is designed to provide clear, consistent feedback to API clients and to ensure robust
processing of requests.

- **ControllerExceptionHandler**
    - **Responsibility:** Centralizes exception handling for all controllers.
    - **Action:** Catches and maps exceptions to appropriate HTTP responses, including Problem Details (RFC 7807) where
      applicable.

- **Custom Exceptions**
    - **InvalidPayloadException:** Thrown when incoming requests fail validation or contain malformed data.
    - **BadGatewayException:** Used to signal issues when communicating with downstream services or Kafka.

- **Validation**
    - Incoming requests are validated in the controller layer. If validation fails, an `InvalidPayloadException` is
      thrown and handled globally.

- **Problem Details**
    - Error responses follow the [RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807) format,
      providing standardized error information (type, title, status, detail, instance).

- **Logging**
    - All errors are logged with relevant context (e.g., request ID, error details) to aid in troubleshooting and
      monitoring.

This approach ensures that clients receive meaningful error messages and that failures are handled gracefully and
consistently across all endpoints.

## Docker support

Pull image from private CH registry by
running `docker pull 416670754337.dkr.ecr.eu-west-2.amazonaws.com/chs-kafka-api-java:latest` command
or run the following steps to build image locally:

1. `export SSH_PRIVATE_KEY_PASSPHRASE='[your SSH key passhprase goes here]'` (optional, set only if SSH key is
   passphrase protected)
2.

`DOCKER_BUILDKIT=0 docker build --build-arg SSH_PRIVATE_KEY="$(cat ~/.ssh/id_rsa)" --build-arg SSH_PRIVATE_KEY_PASSPHRASE -t 416670754337.dkr.ecr.eu-west-2.amazonaws.com/chs-kafka-api-java:latest .`

## Configuration

### Kafka Topics

- **Standard Topic**: For normal message processing.

### Environment Variables

| Variable               | Description                                   | Example          |
|------------------------|-----------------------------------------------|------------------|
| PORT                   | The port at which the service is hosted       | 8081             |
| BOOTSTRAP_SERVER_URL   | The URL to the Kafka broker                   | localhost:9092   |
| FILING_PROCESSED_TOPIC | The Kafka topic for filing processed messages | filing-processed |
| MESSAGE_SEND_TOPIC     | The Kafka topic for message send messages     | message-send     |

