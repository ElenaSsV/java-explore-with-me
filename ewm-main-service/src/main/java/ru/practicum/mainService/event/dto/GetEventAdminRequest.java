package ru.practicum.mainService.event.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import ru.practicum.mainService.event.model.State;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class GetEventAdminRequest {
    private Set<Long> users;
    private List<State> states;
    private Set<Long> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private PageRequest pageRequest;


    public static GetEventAdminRequest of(Set<Long> users, List<String> statesStr, Set<Long> categories, String startStr,
                                          String endStr, PageRequest pageRequest) {
        GetEventAdminRequest eventRequest = new GetEventAdminRequest();
        eventRequest.setPageRequest(pageRequest);
        if (users != null) {
            eventRequest.setUsers(users);
        }
        if (statesStr != null) {
            List<State> convertedStates = new ArrayList<>();
                for (String state : statesStr) {
                    convertedStates.add(State.from(state).get());
                }
            eventRequest.setStates(convertedStates);
        }

        if (categories != null) {
            eventRequest.setCategories(categories);
        }

        if (startStr != null) {
            eventRequest.setRangeStart(LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (endStr != null) {
            eventRequest.setRangeEnd(LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return eventRequest;
    }

    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    public boolean hasStates() {
        return states != null && !states.isEmpty();
    }

    public boolean hasCategories() {
        return categories != null && !categories.isEmpty();
    }

    public boolean hasRangeStart() {
        return rangeStart != null;
    }

    public boolean hasRangeEnd() {
        return rangeEnd != null;
    }
}
