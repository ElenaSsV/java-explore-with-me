package ru.practicum.mainService.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.event.model.QEvent;
import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.participationRequest.mapper.RequestMapper;
import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.mapper.EventMapper;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.event.model.StateAction;
import ru.practicum.mainService.exception.DataConflictException;
import ru.practicum.mainService.exception.InputValidationException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.event.location.Location;
import ru.practicum.mainService.participationRequest.model.ParticipationRequest;
import ru.practicum.mainService.participationRequest.model.RequestStatus;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.category.repository.CategoryRepository;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.event.location.LocationRepository;
import ru.practicum.mainService.participationRequest.repository.ParticipationRequestRepository;
import ru.practicum.mainService.user.repository.UserRepository;
import ru.practicum.statClient.StatServiceClient;
import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsDto.ViewStats;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class EventServiceImpl implements EventService {

    private static final String APP_NAME = "ewm-main-service";
    private static final String START = "2020-05-05 00:00:00";
    private static final String END = "2035-05-05 00:00:00";
    private final StatServiceClient statsClient;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository requestRepository;
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public EventFullDto postNewEvent(long userId, NewEventDto newEventDto) {
        log.info("Posting event={}", newEventDto);

        checkEventTime(newEventDto.getEventDate());

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id=" + userId + " is not found."));

        Category category = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(() ->
                new NotFoundException("Category with id=" + newEventDto.getCategory() + " is not found."));

        Location location = locationRepository.save(newEventDto.getLocation());

        Event eventToPost = EventMapper.toEvent(newEventDto, category, user, location);
        log.info("Event to post={}", eventToPost);

        return EventMapper.toEventFullDto(eventRepository.save(eventToPost));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(long eventId, UpdateEventAdminRequest updateRequest)  {
        log.info("Updating event with id={} by {} via admin API", eventId, updateRequest);

        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " is not found"));

        if (updateRequest.getEventDate() != null) {
            checkEventTime(updateRequest.getEventDate());
        }
        Optional<Category> category;
        if (updateRequest.getCategory() != null) {
            category = categoryRepository.findById(updateRequest.getCategory());
        } else {
            category = Optional.empty();
        }

        if (updateRequest.getStateAction() != null) {
            StateAction action = updateRequest.getStateAction();

            if (action.equals(StateAction.PUBLISH_EVENT) && !eventToUpdate.getState().equals(State.PENDING)) {
                throw new DataConflictException("Cannot update event state. Event can be published only if it has state=PENDING. " +
                        "Event state is " + eventToUpdate.getState() + ".");
            }

            if (action.equals(StateAction.REJECT_EVENT) && eventToUpdate.getState().equals(State.PUBLISHED)) {
                throw new DataConflictException("Cannot update event state. Event cannot be cancelled when it has state=PUBLISHED");
            }
        }

        Event updatedEvent = EventMapper.toEventFromEventUpdateAdminRequest(eventToUpdate, updateRequest, category);
        log.info("Updated event={}", updatedEvent);

        return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    @Override
    public List<EventFullDto> getEventsByAdmin(GetEventAdminRequest eventRequest) {
        log.info("Retrieving events with the following search params={}", eventRequest);

        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();

        if (eventRequest.hasUsers()) {
            conditions.add(event.initiator.id.in(eventRequest.getUsers()));
        }

        if (eventRequest.hasStates()) {
            conditions.add(event.state.in(eventRequest.getStates()));
        }

        if (eventRequest.hasCategories()) {
            conditions.add(event.category.id.in(eventRequest.getCategories()));
        }

        if (eventRequest.hasRangeStart()) {
            conditions.add(event.eventDate.after(eventRequest.getRangeStart()));
        }

        if (eventRequest.hasRangeEnd()) {
            conditions.add(event.eventDate.before(eventRequest.getRangeEnd()));
        }

        Optional<BooleanExpression> finalCondition = conditions.stream()
                .reduce(BooleanExpression::and);
        log.info("FinalCondition={}", finalCondition);

        return finalCondition.map(booleanExpression -> eventRepository.findAll(booleanExpression, eventRequest.getPageRequest()).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList())).orElseGet(() -> eventRepository.findAll(eventRequest.getPageRequest()).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList()));
    }

    @Override
    public List<EventFullDto> getEventsByCurrentUser(long userId, PageRequest pageRequest) {
        log.info("Retrieving events by user with id={}, pageRequest={}", userId, pageRequest);

        return eventRepository.findByInitiator_id(userId, pageRequest).stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdByCurrentUser(long userId, long eventId) {
        log.info("Retrieving event with id={} by user with id={}", eventId, userId);

        Event searchedEvent = eventRepository.findByIdAndInitiator_id(eventId, userId).orElseThrow(() ->
                new NotFoundException("Event with id= " + eventId + " which has initiator id= " + userId + " is not found"));

        return EventMapper.toEventFullDto(searchedEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest request)  {
        log.info("Updating event with id={} by user with id={} with request={}", eventId, userId, request);

        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " is not found"));
        if (eventToUpdate.getState().equals(State.PUBLISHED)) {
            throw new DataConflictException("Event cannot be changed after it has been published.");
        }
        if (request.getEventDate() != null) {
            checkEventTime(request.getEventDate());
        }
       Optional<Category> category;
        if (request.getCategory() != null) {
            category = categoryRepository.findById(request.getCategory());
        } else {
            category = Optional.empty();
        }

        Event updatedEvent = EventMapper.toEventFromEventUpdateUserRequest(eventToUpdate, request, category);

        return EventMapper.toEventFullDto(eventRepository.save(updatedEvent));
    }

    @Override
    public List<ParticipationRequestDto> getParticipationRequestsToEvent(long userId, long eventId) {
        if (!eventRepository.existsByIdAndInitiator_id(eventId, userId)) {
            throw new NotFoundException("Event with id= " + eventId + " which has initiator id= "
                    + userId + " is not found");
        }
        return requestRepository.findAllByEvent_Id(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventParticipationRequestStatus(long userId, long eventId,
                                                                                EventRequestStatusUpdateRequest request) {

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId
        + " is not found."));

        if (event.getInitiator().getId() != userId) {
            throw new NotFoundException("Event with id= " + eventId + " which has initiator id= " + userId + " is not found");
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        RequestStatus newStatus = request.getStatus();
        if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
            return result;
        }
        if (event.getConfirmedRequests() == event.getParticipantLimit() && newStatus.equals(RequestStatus.CONFIRMED)) {
            throw new DataConflictException("Confirmed participants qty has already reached the limit");
        }

        Set<Long> requestIdsToChange = request.getRequestIds();
        if (requestIdsToChange.isEmpty()) {
            return result;
        }

        List<ParticipationRequest> foundRequests = requestRepository.findAllById(requestIdsToChange);

        List<ParticipationRequest> savedRequests = new ArrayList<>();


        for (ParticipationRequest pRequest : foundRequests) {
            if (!pRequest.getStatus().equals(RequestStatus.PENDING)) {
                throw new DataConflictException("Request status cannot be changed. Current status: " + pRequest.getStatus());
            }

            if (event.getConfirmedRequests() == event.getParticipantLimit() && newStatus.equals(RequestStatus.CONFIRMED)) {
                pRequest.setStatus(RequestStatus.REJECTED);
                ParticipationRequest savedRequest = requestRepository.save(pRequest);
                savedRequests.add(savedRequest);
            }
            pRequest.setStatus(newStatus);
            ParticipationRequest savedRequest2 = requestRepository.save(pRequest);
            savedRequests.add(savedRequest2);
        }

        result.setConfirmedRequests(savedRequests
                .stream()
                .filter(foundRequest -> foundRequest.getStatus().equals(RequestStatus.CONFIRMED))
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList()));

        result.setRejectedRequests(savedRequests
                .stream()
                .filter(foundRequest -> foundRequest.getStatus().equals(RequestStatus.REJECTED))
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList()));
        return result;
    }

    @Override
    public List<EventShortDto> getEventsByAnyUser(GetEventPublicRequest eventRequest, PageRequest pageRequest,
                                                  HttpServletRequest request) {
        sendEndpointHit(request);
        QEvent event = QEvent.event;
        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(State.PUBLISHED));

        if (eventRequest.hasText()) {
            conditions.add(event.annotation.containsIgnoreCase(eventRequest.getText())
                    .or(event.description.containsIgnoreCase(eventRequest.getText())));
        }

        if (eventRequest.hasCategories()) {
            conditions.add(event.category.id.in(eventRequest.getCategories()));
        }

        if (eventRequest.hasPaid()) {
            conditions.add(makePaidCondition(eventRequest.getPaid()));
        }
        if (eventRequest.hasRangeStart()) {
            conditions.add(event.eventDate.after(eventRequest.getRangeStart()));
        } else {
            conditions.add(event.eventDate.after(LocalDateTime.now()));
        }

        if (eventRequest.hasRangeEnd()) {
            conditions.add(event.eventDate.before(eventRequest.getRangeEnd()));
        }

        if (eventRequest.hasOnlyAvailable() && eventRequest.getOnlyAvailable().equals(true)) {
            conditions.add(event.participantLimit.ne(event.confirmedRequests));
        }

        BooleanExpression finalCondition = conditions.stream()
                .reduce(BooleanExpression::and)
                .get();

        List<Event> foundEvents = eventRepository.findAll(finalCondition, pageRequest).toList();

        List<Event> eventsWithViews = addViewsToEventsList(foundEvents);
        sendEndPointHits(eventsWithViews, request);


        if (eventRequest.getSort() == null) {
            return eventsWithViews.stream()
                    .map(EventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }
        GetEventPublicRequest.Sort sort = eventRequest.getSort();

        switch (sort) {
            case VIEWS:
                return  eventsWithViews.stream()
                        .map(EventMapper::toEventShortDto)
                        .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                        .collect(Collectors.toList());
            case EVENT_DATE:
                return eventsWithViews.stream()
                        .map(EventMapper::toEventShortDto)
                        .sorted(Comparator.comparing(EventShortDto::getEventDate).reversed())
                        .collect(Collectors.toList());
            default:
                return eventsWithViews.stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto getEventByIdAnyUser(long eventId, HttpServletRequest request) {
        Event searchedEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Event with id=" + eventId + " is not found."));
        if (!searchedEvent.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " is not found.");
        }
        Event eventWithViews = addViewsToEvent(searchedEvent, request);
        sendEndpointHit(request);

        return EventMapper.toEventFullDto(eventWithViews);
    }

    private void checkEventTime(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InputValidationException("Cannot publish event because it is less than 2 hrs before event");
        }
    }

    private BooleanExpression makePaidCondition(Boolean paid) {
        if (paid.equals(true)) {
            return QEvent.event.paid.isTrue();
        } else {
            return QEvent.event.paid.isFalse();
        }
    }

    private void sendEndpointHit(HttpServletRequest request) {
        EndPointHitDto endPointHitDto = new EndPointHitDto(0L, APP_NAME, request.getRequestURI(),
                request.getRemoteAddr(), LocalDateTime.now());

        statsClient.saveEndPointHit(endPointHitDto);
    }

    private Event addViewsToEvent(Event retrievedEvent, HttpServletRequest request) {
        List<ViewStats> getHits = statsClient.getStatistics(START, END, Collections.singletonList(request.getRequestURI()),
        true);

        log.info("Received getHits: {}", getHits);
        if (!getHits.isEmpty()) {
            retrievedEvent.setViews(getHits.get(0).getHits());
        }
        return retrievedEvent;
    }

    private List<Event> addViewsToEventsList(List<Event> retrievedEvents) {
        String path = "/events/";
        List<String> urisToSearch = new ArrayList<>();
        Map<String, Event> eventsByUris = new HashMap<>();

        for (Event retrievedEvent : retrievedEvents) {
            urisToSearch.add(path + retrievedEvent.getId());
            eventsByUris.put(path + retrievedEvent.getId(), retrievedEvent);
        }

        List<ViewStats> statsForEvents = statsClient.getStatistics(START, END, urisToSearch, true);
        if (statsForEvents.isEmpty()) {
            return retrievedEvents;
        }

        for (ViewStats viewStat : statsForEvents) {
            if (eventsByUris.containsKey(viewStat.getUri())) {
                eventsByUris.get(viewStat.getUri()).setViews(viewStat.getHits());
            }
        }

        return new ArrayList<>(eventsByUris.values());
    }

    private void sendEndPointHits(List<Event> viewedEvents, HttpServletRequest request) {
        for (Event viewedEvent : viewedEvents) {
            statsClient.saveEndPointHit(new EndPointHitDto(0L, APP_NAME, "/events/" + viewedEvent.getId(),
                    request.getRemoteAddr(), LocalDateTime.now()));
        }
    }

}
