package ru.practicum.mainService.comment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainService.comment.model.CommentSort;
import ru.practicum.mainService.exception.InputValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Data
@NoArgsConstructor
public class GetCommentsAdminRequest {
    private Long eventId;
    private Boolean edited;
    private Long authorId;
    private String text;
    private CommentSort commentSort;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;


    public static GetCommentsAdminRequest of(Long eventId, Boolean edited, Long authorId, String text, String sort, String startStr,
                                             String endStr) {
        GetCommentsAdminRequest adminRequest = new GetCommentsAdminRequest();
        if (eventId != null) {
            adminRequest.setEventId(eventId);
        }
        if (edited != null) {
            adminRequest.setEdited(edited);
        }

        if (authorId != null) {
            adminRequest.setAuthorId(authorId);
        }

        if (text != null && !text.isBlank() && !text.isEmpty()) {
            adminRequest.setText(text);
        }

        Optional<CommentSort> optionalCommentSort = CommentSort.from(sort);
        if (optionalCommentSort.isPresent()) {
            adminRequest.setCommentSort(optionalCommentSort.get());
        } else {
            throw new InputValidationException("Unknown sort: " + sort);
        }


        if (startStr != null) {
            adminRequest.setRangeStart(LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (endStr != null) {
            adminRequest.setRangeEnd(LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        return adminRequest;
    }

    public boolean hasEventId() {
        return eventId != null;
    }

    public boolean hasEdited() {
        return edited != null;
    }

    public boolean hasAuthorId() {
        return authorId != null;
    }

    public boolean hasText() {
        return text != null && !text.isBlank() && !text.isEmpty();
    }

    public boolean hasRangeStart() {
        return rangeStart != null;
    }

    public boolean hasRangeEnd() {
        return rangeEnd != null;
    }
}
