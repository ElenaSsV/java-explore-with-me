package ru.practicum.statsService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsDto.ViewStats;
import ru.practicum.statsService.service.StatService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatServiceController {

    private final StatService statService;

    @PostMapping("/hit")
    public EndPointHitDto post(@RequestBody @Valid EndPointHitDto endPointHitDto) {
        log.info("Posting endPointHitDto {}", endPointHitDto);
        return statService.save(endPointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> get(@NotBlank @RequestParam(name = "start") String start,
                               @NotBlank @RequestParam(name = "end") String end,
                               @RequestParam(name = "uris", required = false) List<String> uris,
                               @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {

        String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);

        log.info("Retrieving statistics with following params: start {}, end {}, uris {}, unique {}", decodedStart,
                decodedEnd, uris, unique);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.parse(decodedStart, formatter);
        LocalDateTime endTime = LocalDateTime.parse(decodedEnd, formatter);

        return statService.getStatistics(startTime, endTime, Objects.requireNonNullElseGet(uris, ArrayList::new), unique);
    }
}
