## Introduction
- Project utilize **micrometer** — best in class application monitoring framework for Java applications
- Prometheus collects metrics from http://localhost:8080/actuator/prometheus
- Custom metrics samples are founded in src/main/java/io/opsguru/prom/actuator/controller/SampleController.java
- More custom metric samples could be found at https://micrometer.io/docs

## Setup
- set host ip in docker/prometheus/prometheus.yml
- cd docker && docker-compose up
- mvn spring-boot:run
- visit a couple of times endpoints to collect metrics or use script from bin/bootstrap-metrics.sh (for unix environments only)

## Endpoints
- http://localhost:8080/api/v1/hello
- http://localhost:8080/api/v1/hello?name=Dima
- http://localhost:8080/api/v1/failure
- http://localhost:8080/api/v1/slow
- http://localhost:8080/api/v2/slow
- http://localhost:8080/docs/swagger
