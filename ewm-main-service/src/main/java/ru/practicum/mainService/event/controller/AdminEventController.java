package ru.practicum.mainService.event.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.GetEventAdminRequest;
import ru.practicum.mainService.event.dto.UpdateEventAdminRequest;
import ru.practicum.mainService.event.service.EventService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Validated
@Slf4j
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long  eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateRequest) throws JsonMappingException {
        log.info("Received request to update event with following params: eventId={}, updateRequest={}", eventId, updateRequest);
        return eventService.updateEventByAdmin(eventId, updateRequest);
    }

    @GetMapping
    public List<EventFullDto> searchEvents(@RequestParam(name = "users", required = false) Set<Long> users,
                                           @RequestParam(name = "states", required = false) List<String> states,
                                           @RequestParam(name = "categories", required = false) Set<Long> categories,
                                           @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                           @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        PageRequest page = PageRequest.of(from / size, size);

        log.info("Received request to get events with the following params: users={}, states={}, categories={}, " +
                "rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories, rangeStart, rangeEnd,
                from, size);

        return eventService.getEventsByAdmin(GetEventAdminRequest.of(users, states, categories, rangeStart, rangeEnd, page));
    }

}
