package ru.practicum.mainService.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    private Set<Long> events;
    private Boolean pinned;
    @NotBlank(message = "Title cannot be null or empty")
    @Size(min = 1, max = 50, message = "Title should not be less than 1 symbol and more than 50 symbols")
    private String title;

}
