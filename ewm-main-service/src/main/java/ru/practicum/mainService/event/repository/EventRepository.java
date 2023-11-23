package ru.practicum.mainService.event.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainService.event.model.Event;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findByInitiator_idIn(Set<Long> ids, Pageable page);

    Page<Event> findByInitiator_id(long id, Pageable page);

    Optional<Event> findByIdAndInitiator_id(long eventId, long initiatorId);

    boolean existsByIdAndInitiator_id(long eventId, long initiatorId);
}
