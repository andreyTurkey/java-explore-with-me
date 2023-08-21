package ru.practicum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class EwmClient extends BaseClient {

    public EwmClient(@Value("${ewm-stats-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addHit(HitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(List<String> uris, String start, String end, boolean unique) {
        Map<String, Object> parameters;
        if (uris == null) {
            parameters = Map.of(
                    "start", encodeLocalDateTime(start),
                    "end", encodeLocalDateTime(end),
                    "unique", unique);
            return getStat("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            parameters = Map.of("start", encodeLocalDateTime(start),
                    "end", encodeLocalDateTime(end),
                    "uris", uris,
                    "unique", unique);
            return getStat("/stats?start={start}&end={end}&unique={unique}&uris={uris}", parameters);
        }
    }

    private String encodeLocalDateTime(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

