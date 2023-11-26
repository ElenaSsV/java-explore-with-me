package ru.practicum.mainService.participationRequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.participationRequest.service.ParticipationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Validated
@Slf4j
public class PrivateParticipationRequestController {

    private final ParticipationRequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto postNewRequest(@PathVariable Long userId,
                                                  @RequestParam(name = "eventId") Long eventId) {
        log.info("Received request to post participation request to event id={} by user with id={}", eventId, userId);
        return requestService.postNewRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Received request to cancel participation request with the following params: userId={}, requestId={}",
                userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("Received request to get participation requests by user with id={}", userId);
        return requestService.getRequests(userId);
    }
}
