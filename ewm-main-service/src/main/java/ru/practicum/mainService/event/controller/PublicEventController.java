package ru.practicum.mainService.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.dto.GetEventPublicRequest;
import ru.practicum.mainService.event.service.EventService;
import ru.practicum.mainService.exception.InputValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class PublicEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEventsByAnyUser(@RequestParam(name = "text", required = false) String text,
                                                  @RequestParam(name = "categories", required = false) Set<Long> categories,
                                                  @RequestParam(name = "paid", required = false) Boolean paid,
                                                  @RequestParam(name = "rangeStart", required = false) String rangeStart,
                                                  @RequestParam(name = "rangeEnd", required = false) String rangeEnd,
                                                  @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam(name = "sort", required = false) String sort,
                                                  @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                  @RequestParam(name = "size", required = false, defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        log.info("Received request to get events with the following params: text={}, categpries={}, paid={}, rangeStart={}," +
                "rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);

        checkText(text);

        PageRequest page = PageRequest.of(from, size);

        return eventService.getEventsByAnyUser(GetEventPublicRequest.of(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort), page, request);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventByIdAnyUser(@PathVariable Long id, HttpServletRequest request) {
        log.info("Received request to get events with the following params: id={}, request={}", id, request);

        return eventService.getEventByIdAnyUser(id, request);
    }

    private void checkText(String text) {
        if (text != null) {
            char[] chars = text.toCharArray();
            for (char c : chars) {
                if (Character.isDigit(c)) {
                    throw new InputValidationException("Text should  contain only letters.");
                }
            }
        }
    }
}
