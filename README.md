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

## End Point Flow

### Filing Processed Feature Flow

The Filing Processed feature allows internal services to submit processed filing information via a dedicated HTTP POST
endpoint. Incoming requests containing a ProcessedFiling Java model are validated, mapped to the Avro FilingProcessed
schema (with support for date/time transformations), and published to the filing-processed Kafka topic.

### Message Send Feature Flow

The Message Send feature enables internal services to send generic messages via a dedicated HTTP POST endpoint. Incoming
requests containing a MessageSend Java model are validated, mapped to the Avro message_send schema, and published to the
message-send Kafka topic.


### Email Send Feature Flow

The Email Send feature enables internal services to send email fields to the application via a dedicated HTTP POST 
endpoint. Incoming requests containing a SendEmail Java model are validated, mapped to the Avro email_send schema, and 
published to the email-send Kafka topic.

### Endpoints Overview

| Endpoint URI                | HTTP Method | Request Body Model | Avro Model Published | Kafka Topic      |
|-----------------------------|-------------|--------------------|----------------------|------------------|
| `/private/filing-processed` | POST        | ProcessedFiling    | FilingProcessed      | filing-processed |
| `/message-send`             | POST        | MessageSend        | message_send         | message-send     |
| `/send-email`               | POST        | SendEmail          | email_send           | email-send       |

---

### Error Handling

Error handling in this service is designed to provide clear, consistent feedback to API clients and to ensure robust
processing of requests.

- **ControllerExceptionHandler**
    - **Responsibility:** Centralizes exception handling for all controllers.
    - **Action:** Catches and maps exceptions to appropriate HTTP responses, including Problem Details (RFC 7807) where
      applicable.

- **Custom Exceptions**
    - **InvalidPayloadException:** Thrown when incoming requests contain malformed data.
    - **BadGatewayException:** Used to signal issues when communicating with downstream services or Kafka.

- **Validation**
    - Incoming requests are validated in the controller layer. If validation fails, an `MethodArgumentNotValidException`
      is
      thrown and handled globally.

- **Problem Details**
    - Error responses follow the [RFC 7807 Problem Details](https://datatracker.ietf.org/doc/html/rfc7807) format,
      providing standardized error information (type, title, status, detail, instance).

- **Logging**
    - All errors are logged with relevant context (e.g., request ID, error details) to aid in troubleshooting and
      monitoring.

This approach ensures that clients receive meaningful error messages and that failures are handled gracefully and
consistently across all endpoints.

## Getting started

To checkout and build the service:

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the
   README.
2. Run `./bin/chs-dev services enable chs-kafka-api-java`
3. Run `./bin/chs-dev development enable chs-kafka-api-java` if you wish to see changes in the code dynamically
4. Run `chs-dev up` in the docker-chs-development directory.

These instructions are for a local docker environment.

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

