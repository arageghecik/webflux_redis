package com.example.webflux_redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class PlaneFinderPoller {
    private WebClient client = WebClient.create("http://localhost:7634/aircraft");

    private final RedisConnectionFactory connectionFactory;
    private final AircraftRepository aircraftRepository;

    @Scheduled(fixedRate = 9000)
    private void pollPlanes() {
        //clear redis all data
        connectionFactory.getConnection().serverCommands().flushDb();
        //flux reactive data type
        //flux is publisher in reactive approach, we get data from webclient in flux form
        Flux<Aircraft> aircraftFlux = client.get().retrieve().bodyToFlux(Aircraft.class);

        //storing data to redis
        aircraftFlux.filter(plane -> !plane.getReg().isEmpty())
                .toStream().forEach(aircraftRepository::save);

        // taking out data from redis
        aircraftRepository.findAll().forEach(System.out::println);
    }

}
