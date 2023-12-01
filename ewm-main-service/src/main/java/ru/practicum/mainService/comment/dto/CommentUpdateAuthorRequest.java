package ru.practicum.mainService.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateAuthorRequest {

    @NotBlank(message = "Text cannot be empty or blank.")
    @Size(min = 2, max = 1000, message = "Text should be min 2 symbols, max 1000 symbols")
    private String text;
}
