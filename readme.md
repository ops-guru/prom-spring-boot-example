1. set host ip in docker/prometheus/prometheus.yml
2. cd docker && docker-compose up
3. mvn spring-boot:run
4. visit a couple of times http://localhost:8080/?name=Foobar, http://localhost:8080/?name=FizzBuzz and http://localhost:8080/slow_api to collect custom metrics.

custom metrics samples are founded in src/main/java/io/opsguru/prom/actuator/controller/SampleController.java