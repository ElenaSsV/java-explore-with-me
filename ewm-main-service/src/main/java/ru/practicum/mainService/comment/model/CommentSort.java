package ru.practicum.mainService.comment.model;


import java.util.Optional;

public enum CommentSort {
    OLDEST,
    NEWEST;

    public static Optional<CommentSort> from(String stringState) {
        for (CommentSort sort : values()) {
            if (sort.name().equalsIgnoreCase(stringState)) {
                return Optional.of(sort);
            }
        }
        return Optional.empty();
    }
}
