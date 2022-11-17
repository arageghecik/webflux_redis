package com.example.webflux_redis;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Scheduled(fixedRate = 100)
    private void pollPlanes() {
        connectionFactory.getConnection().serverCommands().flushDb();

        client.get().retrieve().bodyToFlux(Aircraft.class)
                .filter(plane -> !plane.getReg().isEmpty()).toStream()
                .forEach(ac -> redisOperations.opsForValue().set(ac.getReg(), ac));

        redisOperations.opsForValue().getOperations().keys("*")
                .forEach(ac -> System.out.println(redisOperations.opsForValue().get(ac)));
    }

}
