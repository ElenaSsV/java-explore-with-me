package ru.practicum.mainService.event.model;

import java.util.Optional;

public enum State {
    PUBLISHED,
    PENDING,
    CANCELED;

    public static Optional<State> from(String stringState) {
        for (State state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}

