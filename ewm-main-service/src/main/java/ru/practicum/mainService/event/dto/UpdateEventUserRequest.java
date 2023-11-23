package ru.practicum.mainService.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.mainService.event.model.StateAction;
import ru.practicum.mainService.event.location.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, message = "Minimum symbols=20, maximum symbols=2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Minimum symbols=20, maximum symbols=7000")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    @Size(min = 3, max = 120, message = "Minimum symbols=3, maximum symbols=120")
    private String title;
}


