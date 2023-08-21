package ru.practicum.publicPart;

import org.springframework.stereotype.Service;

@Service
public class PublicClient  {
   /* private static final String API_PREFIX = "/hit";

    @Autowired
    public PublicClient(@Value("${stat-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> endpointEventRequest(HitDto hitDto) {
        return post("", hitDto);
    }

    public ResponseEntity<Object> endpointEventIdRequest(HitDto hitDto) {
        return post("", hitDto);
    }*/
}
