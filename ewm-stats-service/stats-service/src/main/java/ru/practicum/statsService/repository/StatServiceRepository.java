package ru.practicum.statsService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statsDto.ViewStats;
import ru.practicum.statsService.model.EndPointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServiceRepository extends JpaRepository<EndPointHit, Long> {

    @Query("select new ru.practicum.statsDto.ViewStats (e.app, e.uri, count(e.ip) as c) " +
            "from EndPointHit as e where created between ?1 and ?2 group by e.uri, e.app order by c desc")
    List<ViewStats> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statsDto.ViewStats (e.app, e.uri, count(distinct(e.ip)) as c) " +
            "from EndPointHit as e where created between ?1 and ?2 group by e.uri, e.app order by c desc")
    List<ViewStats> findByTimestampBetweenForUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.statsDto.ViewStats (e.app, e.uri, count(e.ip) as c) " +
            "from EndPointHit as e where created between ?1 and ?2 and uri in ?3 group by e.uri, e.app order by c desc")
    List<ViewStats> findByTimestampBetweenAndUrisIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.statsDto.ViewStats (e.app, e.uri, count(distinct(e.ip)) as c) " +
            "from EndPointHit as e where created between ?1 and ?2 and uri in ?3 group by e.uri, e.app order by c desc")
    List<ViewStats> findByTimestampBetweenAndUrisInForUniqueIp(LocalDateTime start, LocalDateTime end, List<String> uris);

}
