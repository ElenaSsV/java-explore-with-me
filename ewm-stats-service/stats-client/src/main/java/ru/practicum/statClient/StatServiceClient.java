package ru.practicum.statClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.statsDto.EndPointHitDto;

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

    public StatServiceClient(RestTemplateBuilder builder) {
        ret =
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:9090"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build();
    }

    public ResponseEntity<Object> saveEndPointHit(EndPointHitDto endPointHitDto) {
        return makeAndSendRequest(HttpMethod.POST, HIT_PATH, null, endPointHitDto);
    }

    public ResponseEntity<Object> getStatistics(String start, String end, List<String> uris, Boolean unique) {

        String encodedStart = URLEncoder.encode(start, StandardCharsets.UTF_8);
        String encodedEnd = URLEncoder.encode(end, StandardCharsets.UTF_8);

        Map<String, Object> parameters = Map.of(
                "start", encodedStart,
                "end", encodedEnd,
                "uris", uris,
                "unique", unique
        );

        if (uris.isEmpty()) {
            return makeAndSendRequest(HttpMethod.GET, STATS_PATH + "?start={start}&end={end}&unique={unique}",
                    parameters, null);
        }
        return makeAndSendRequest(HttpMethod.GET, STATS_PATH + "?start={start}&end={end}&uris={uris}&unique={unique}",
                parameters, null);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          T body) {
        ResponseEntity<Object> statServiceResponse;
        try {
            if (parameters != null && body != null) {
                HttpEntity<T> requestEntity = new HttpEntity<>(body);
                statServiceResponse = ret.exchange(path, method, requestEntity, Object.class, parameters);
                log.info("StatServiceClient received response {}", statServiceResponse.getBody());
            } else {
                statServiceResponse = ret.exchange(path, method, null, Object.class);
                log.info("StatServiceClient received response {}", statServiceResponse.getBody());
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareGatewayResponse(statServiceResponse);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }
        return responseBuilder.build();
    }

}
