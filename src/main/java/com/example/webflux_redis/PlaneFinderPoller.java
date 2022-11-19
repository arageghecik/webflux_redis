package com.example.webflux_redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@EnableScheduling
public class PlaneFinderPoller {
    private WebClient client = WebClient.create("http://localhost:7634/aircraft");

    public PlaneFinderPoller(RedisConnectionFactory connectionFactory, RedisOperations<String, Aircraft> redisOperations) {
        this.connectionFactory = connectionFactory;
        this.redisOperations = redisOperations;
    }

    private final RedisConnectionFactory connectionFactory;
    private final RedisOperations<String, Aircraft> redisOperations;

    @Scheduled(fixedRate = 9000)
    private void pollPlanes() {
        //clear redis all data
        connectionFactory.getConnection().serverCommands().flushDb();
        //flux is publisher in reactive approach, we get data from webclient in flux form
        Flux<Aircraft> aircraftFlux = client.get().retrieve().bodyToFlux(Aircraft.class);

        //storing data to redis
        aircraftFlux.filter(plane -> !plane.getReg().isEmpty())
                //.subscribe(ac -> redisOperations.opsForValue().set(ac.getReg(), ac)); // this variant didn't work
                .toStream().forEach(ac -> redisOperations.opsForValue().set(ac.getReg(), ac));

        // taking out data from redis
        redisOperations.opsForValue().getOperations().keys("*").forEach(ac -> System.out.println(redisOperations.opsForValue().get(ac)));
    }

}
