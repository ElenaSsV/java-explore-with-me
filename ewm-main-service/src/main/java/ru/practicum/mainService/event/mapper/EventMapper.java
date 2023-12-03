package ru.practicum.mainService.event.mapper;

import ru.practicum.mainService.category.mapper.CategoryMapper;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.comment.dto.CommentDto;
import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.location.Location;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        Event event = new Event();
        event.setInitiator(user);
        event. setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(location);
        event.setTitle(newEventDto.getTitle());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        return event;
    }

    public static EventFullDto toEventFullDto(Event event, List<CommentDto> comments) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        fullDto.setConfirmedRequests(event.getConfirmedRequests());
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setDescription(event.getDescription());
        fullDto.setEventDate(event.getEventDate());
        fullDto.setId(event.getId());
        fullDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        fullDto.setLocation(event.getLocation());
        fullDto.setPaid(event.getPaid());
        fullDto.setParticipantLimit(event.getParticipantLimit());
        if (event.getPublishedOn() != null) {
            fullDto.setPublishedOn(event.getPublishedOn());
        }
        fullDto.setRequestModeration(event.isRequestModeration());
        fullDto.setState(event.getState());
        fullDto.setTitle(event.getTitle());
        fullDto.setViews(event.getViews());
        fullDto.setComments(comments);

        return fullDto;
    }

    public static EventShortDto toEventShortDto(Event event) {
        EventShortDto dto = new EventShortDto();
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());
        dto.setId(event.getId());
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());
        return dto;
    }

    public static Set<EventShortDto> toEventShortDtoSet(Set<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashSet<>();
        }

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());
    }

    public static Event toEventFromEventUpdateUserRequest(Event event, UpdateEventUserRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
            }
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        return event;
    }

    public static Event toEventFromEventUpdateAdminRequest(Event event, UpdateEventAdminRequest request) {
        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }

        if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }

        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }

        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }

        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    break;
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
            }
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }
        return event;
    }
}
