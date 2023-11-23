package ru.practicum.mainService.participationRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.mainService.participationRequest.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>,
        QuerydslPredicateExecutor<ParticipationRequest> {

    boolean existsByEvent_IdAndRequester_Id(long eventId, long userId);

    List<ParticipationRequest> findAllByRequester_Id(long requesterId);

    List<ParticipationRequest> findAllByEvent_Id(long eventId);

}
