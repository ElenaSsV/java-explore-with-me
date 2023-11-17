package ru.practicum.statsService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsDto.ViewStats;
import ru.practicum.statsService.mapper.EndPointHitMapper;
import ru.practicum.statsService.model.EndPointHit;
import ru.practicum.statsService.repository.StatServiceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {

    private final StatServiceRepository statsRepository;

    public EndPointHitDto save(EndPointHitDto endPointHitDto) {
        EndPointHit endPointHit = EndPointHitMapper.toEndPointHit(endPointHitDto);
        log.info("Saving endPointHit {}", endPointHit);
        return EndPointHitMapper.toEndPointHitDto(statsRepository.save(endPointHit));
    }

    @Override
    public List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Retrieving view statistics with following params: start {}, end {}, uris {}, unique {}",
                start, end, uris, unique);

        if (!uris.isEmpty() && !unique) {
            return statsRepository.findByTimestampBetweenAndUrisIn(start, end, uris);
        } else if (!uris.isEmpty() && unique) {
            return statsRepository.findByTimestampBetweenAndUrisInForUniqueIp(start, end, uris);
        } else if (uris.isEmpty() && unique) {
            return statsRepository.findByTimestampBetweenForUniqueIp(start, end);
        } else {
            return statsRepository.findByTimestampBetween(start, end);
        }
    }
}