package ru.practicum;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    //private final ObjectMapper objectMapper;

    @Value("${stats-server.url}")
    private String serverUrl;

    @Bean
    public EwmClient statsClient() {
        return new EwmClient(serverUrl, new RestTemplateBuilder()/*, objectMapper*/);
    }
}
