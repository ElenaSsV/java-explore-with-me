package ru.practicum.mainService.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Email cannot be blank or null.")
    @Email
    @Size(min = 6, max = 254, message = "Email should not be less than 6 symbols and longer that 254 symbols")
    private String email;
    @NotBlank
    @Size(min = 2, max = 250, message = "Name should not be less than 2 symbols and longer than 250 symbols")
    private String name;
}
