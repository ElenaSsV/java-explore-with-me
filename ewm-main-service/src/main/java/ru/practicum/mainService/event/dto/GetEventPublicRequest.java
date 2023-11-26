package ru.practicum.mainService.event.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Data
@NoArgsConstructor
public class GetEventPublicRequest {
    private String text;
    private Set<Long> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private Sort sort;


    public static GetEventPublicRequest of(String text, Set<Long> categories, Boolean paid, String startStr,
                                          String endStr, Boolean onlyAvailable, String sort) {
        GetEventPublicRequest eventRequest = new GetEventPublicRequest();
        if (text != null) {
            eventRequest.setText(text.toLowerCase());
        }

        if (categories != null) {
            eventRequest.setCategories(categories);
        }

        if (paid != null) {
            eventRequest.setPaid(paid);
        }

        if (startStr != null) {
            eventRequest.setRangeStart(LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (endStr != null) {
            eventRequest.setRangeEnd(LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (onlyAvailable != null) {
            eventRequest.setOnlyAvailable(onlyAvailable);
        }

        if (sort != null) {
            eventRequest.setSort(Sort.valueOf(sort.toUpperCase()));
        }

        return eventRequest;
    }

    public boolean hasText() {
        return text != null && !text.isEmpty();
    }

    public boolean hasPaid() {
        return paid != null;
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

    public boolean hasOnlyAvailable() {
        return onlyAvailable != null;
    }

    public enum Sort { EVENT_DATE, VIEWS }
}
