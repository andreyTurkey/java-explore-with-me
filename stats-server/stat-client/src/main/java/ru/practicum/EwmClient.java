package ru.practicum;

import io.micrometer.core.lang.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public EwmClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
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

    public ResponseEntity<List<ViewStatsDto>> getStats(List<String> uris, String start, String end, @Nullable boolean unique) {
        Map<String, Object> parameters;
        if (uris == null) {
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "unique", unique);
            return getStats("/stats?start={start}&end={end}&unique={unique}", parameters);
        } else {
            String path = "/stats?start={start}&end={end}&unique={unique}&uris=";
            for (String str : uris) {
                path = path + str + "&";
            }
            parameters = Map.of(
                    "start", start,
                    "end", end,
                    "uris", uris,
                    "unique", unique);

            return getStats(path, parameters);
        }
    }

    private String encodeLocalDateTime(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

