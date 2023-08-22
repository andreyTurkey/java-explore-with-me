package ru.practicum.publicPart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.HitDto;

@Slf4j
@Service
public class EwmClientTest {

    WebClient webClient;

    public EwmClientTest(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090").build();
    }

    public ResponseEntity<Object> sendHitDto(HitDto hitDto) {
        webClient.post()
                .uri("/hit")
                .body(Mono.just(hitDto), HitDto.class)
                .retrieve()
                .toEntity(HitDto.class);
                return ResponseEntity.ok().body(hitDto);
    }
}