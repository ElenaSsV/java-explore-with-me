package ru.practicum.mainService.event.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Validated
@Slf4j
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto postNewEvent(@PathVariable Long userId,
                                     @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Received request to post Event {} by user with id={}", newEventDto, userId);
        return eventService.postNewEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsByCurrentUser(@PathVariable Long userId,
                                                     @RequestParam(name = "from", defaultValue = "0") int from,
                                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        PageRequest page = PageRequest.of(from, size);
        log.info("Received request to retrieve events with following params: user id={}, from={}, size={}", userId, from, size);
        return eventService.getEventsByCurrentUser(userId, page);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByIdByCurrentUser(@PathVariable Long userId,
                                                  @PathVariable Long eventId) {
        log.info("Received request to retrieve event with id={} by user with id={}", eventId, userId);
        return eventService.getEventByIdByCurrentUser(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        log.info("Received request to get participation requests to event with id={} by user with id={}", eventId, userId);
        return eventService.getParticipationRequestsToEvent(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest request) throws JsonMappingException {
        log.info("Received request to update event with following params: userId={}, eventId={}, request={}", userId, eventId, request);
        return eventService.updateEventByInitiator(userId, eventId, request);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventParticipationRequest(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @RequestBody EventRequestStatusUpdateRequest request) {

        log.info("Received request to update event with following params: userId={}, eventId={}, request={}", userId, eventId, request);
        return eventService.updateEventParticipationRequestStatus(userId, eventId, request);
    }
}
