package ru.practicum.statClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsDto.ViewStats;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatServiceClient {

    private final RestTemplate ret;
    private static final String HIT_PATH = "/hit";
    private static final String STATS_PATH = "/stats";

    public StatServiceClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        ret =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://ewm-stats-service:9090"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    public void saveEndPointHit(EndPointHitDto endPointHitDto) {
        ret.postForObject(HIT_PATH, endPointHitDto, EndPointHitDto.class);
    }

    public List<ViewStats> getStatistics(String start, String end, List<String> uris, Boolean unique) {

        String encodedStart = URLEncoder.encode(start, StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(end, StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of(
                "start", encodedStart,
                "end", encodedEnd,
                "uris", uris.toArray(),
                "unique", unique
        );

        ResponseEntity<List<ViewStats>> response;

        if (uris.isEmpty()) {
             response = ret.exchange(STATS_PATH + "?start={start}&end={end}&unique={unique}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<ViewStats>>() {}, parameters);
        } else {
            response = ret.exchange(STATS_PATH + "?start={start}&end={end}&uris={uris}&unique={unique}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<List<ViewStats>>() {}, parameters);
        }
        return response.getBody();

    }

}
