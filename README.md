# Purchase Store

Simple project to manage purchase transactions.

## Running the Project

To start the application run `make run`.

It's possible to simulate requests:
- `make request-create` will create a new purchase transaction and print the response.
- `make convert-unit` will convert a unit of a purchase transaction and print the response.

## Main decisions

- The presentation layer its using its own DTOs to avoid exposing the domain model.
- A global error handler is used to handle errors in a consistent way.
- Most validations are done through the DTOs, but some are done in the domain model to ensure business rules are respected.

## Missing features for a production ready application

All the following features are missing for a production ready application.
I did not implement them to keep the project simple and focused on the main functionality and to avoid spending a lot of time on it.

- Traceability of requests. This could be done by:
  - Enabling Actuator endpoints.
  - Adding OpenTelemetry to trace requests done to treasury API.
  - Add traceID to the logs.
- Metrics to monitor the application by enabling Actuator metrics. This also replaces the custom health check.
- Dockerfile to run the application in a container. There are maven plugins that can help with this, such as `spotify/dockerfile-maven` or `jib`.
- Migration scripts to create the database schema. This could be done using a tool like Flyway or Liquibase.
- Application lifecycle release management, like maven release.
- Use undertow instead of the default Tomcat server to reduce the size of the application.
- Better logging and use log4j2.
