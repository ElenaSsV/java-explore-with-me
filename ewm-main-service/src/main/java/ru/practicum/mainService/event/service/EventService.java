package ru.practicum.mainService.event.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.data.domain.PageRequest;
import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto postNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateRequest) throws JsonMappingException;

    List<EventFullDto> getEventsByAdmin(GetEventAdminRequest eventRequest);

    List<EventFullDto> getEventsByCurrentUser(long userId, PageRequest page);

    EventFullDto getEventByIdByCurrentUser(long userId, long eventId);

    EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest request) throws JsonMappingException;

    List<ParticipationRequestDto> getParticipationRequestsToEvent(long userId, long eventId);

    EventRequestStatusUpdateResult updateEventParticipationRequestStatus(long userId, long eventId,
                                                                         EventRequestStatusUpdateRequest request);

    List<EventShortDto> getEventsByAnyUser(GetEventPublicRequest getEventRequest, PageRequest pageRequest, HttpServletRequest request);

    EventFullDto getEventByIdAnyUser(long eventId, HttpServletRequest request);
}
