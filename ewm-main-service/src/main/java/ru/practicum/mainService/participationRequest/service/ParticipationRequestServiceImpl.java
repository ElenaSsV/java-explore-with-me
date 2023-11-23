package ru.practicum.mainService.participationRequest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.participationRequest.mapper.RequestMapper;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.exception.DataConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.participationRequest.model.ParticipationRequest;
import ru.practicum.mainService.participationRequest.model.RequestStatus;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.participationRequest.repository.ParticipationRequestRepository;
import ru.practicum.mainService.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto postNewRequest(long userId, long eventId) {
        log.info("Posting new request to event with id={} by user with id={}", eventId, userId);

        if (requestRepository.existsByEvent_IdAndRequester_Id(eventId, userId)) {
            throw new DataConflictException("User with id=" + userId + " has already sent request to participate " +
                    "in event =" + eventId);
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " is not found."));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " is not found."));

        if (event.getInitiator().getId() == userId) {
            throw new DataConflictException("Event initiator cannot send participation request.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Request can be sent only to published events.");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == event.getConfirmedRequests()) {
            throw new DataConflictException("Participants qty has reached limit");
        }

        ParticipationRequest request = new ParticipationRequest();
        log.info("Adding to request requester={}, event={}", user, event);
        request.setRequester(user);
        request.setEvent(event);

        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        log.info("Posting request{}", request);

        ParticipationRequest savedRequest;
        try {
            savedRequest = requestRepository.save(request);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Constraint violation: " + e.getMessage());
        }
        return RequestMapper.toRequestDto(savedRequest);
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        log.info("Canceling request with id={} by user with id={}", requestId, userId);

        ParticipationRequest requestToUpdate = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Request with id=" + requestId + " is not found."));
        requestToUpdate.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(requestToUpdate));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(long userId) {
        log.info("Retrieving requests by user with id={}", userId);

        return requestRepository.findAllByRequester_Id(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }
}
