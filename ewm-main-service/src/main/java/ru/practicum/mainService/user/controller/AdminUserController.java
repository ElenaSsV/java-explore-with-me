package ru.practicum.mainService.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.user.dto.NewUserRequest;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto postNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Received request to post user={}", newUserRequest);
        return userService.postNewUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Received request to delete user wth id={}", userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsersByIds(@RequestParam(name = "ids", required = false) Set<Long> ids,
                                       @RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        log.info("Received request to get users by following params: ids={}, from={}, size={}", ids, from, size);
        PageRequest page = PageRequest.of(from / size, size);
        return userService.getUsersByIds(ids, page);
    }

}
