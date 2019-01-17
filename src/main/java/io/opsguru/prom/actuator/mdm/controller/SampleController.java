package io.opsguru.prom.actuator.mdm.controller;


import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.TimeUnit;

@RestController
public class SampleController {
    private final PrometheusMeterRegistry registry;
    private final Set<String> names = new ConcurrentSkipListSet<>();
    private final Counter counter;

    @Autowired
    SampleController(PrometheusMeterRegistry registry) {
        this.registry = registry;
        this.counter = registry.counter("received.hellos");
    }

    /*
        README: This method demonstrates collection size gauge (unique_names_count) and counter (received.hellos)
     */
    @GetMapping("/")
    public String sayHello(@RequestParam(value = "name", defaultValue = "Guest") String name) {
        this.counter.increment();
        this.names.add(name);
        this.registry.gaugeCollectionSize("unique_names_count", Tags.empty(), this.names);
        return "Hello " + name + "!!";
    }

    /*
        README: This method demonstrates simple timer (slow.api.timer) and tags (region=us-east-1)
     */
    @GetMapping("/slow_api")
    public String timeConsumingAPI(@RequestParam(value = "delay", defaultValue = "0") Integer delay) throws InterruptedException {
        Timer.Sample sample = Timer.start(registry);
        if(delay == 0) {
            Random random = new Random();
            delay = random.nextInt(10);
        }
        TimeUnit.SECONDS.sleep(delay);
        sample.stop(registry.timer("slow.api.timer", "api.version", "v1"));
        return "Result";
    }

    /*
        README: This method demonstrates simple timer (slow.api.timer) and tags (region=us-east-1)
     */
    @Timed(value = "slow.api.timer", extraTags = {"api.version", "v2"})
    @GetMapping("/slow_api/v1")
    public String timeConsumingAPIV2(@RequestParam(value = "delay", defaultValue = "0") Integer delay) throws InterruptedException {
        if(delay == 0) {
            Random random = new Random();
            delay = random.nextInt(10);
        }
        TimeUnit.SECONDS.sleep(delay);
        return "Result";
    }
}
