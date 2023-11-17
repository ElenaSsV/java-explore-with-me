package ru.practicum.statsService.service;

import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsDto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    EndPointHitDto save(EndPointHitDto endPointHitDto);

    List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
