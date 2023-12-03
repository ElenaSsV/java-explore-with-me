package ru.practicum.mainService.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.service.CompilationService;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilationsByAnyUser(@RequestParam(name = "pinned", required = false) Boolean pinned,
                                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                                         @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Received request to get compilations with the following params: pinned={}, from={}, size={}",
                pinned, from, size);
        PageRequest page = PageRequest.of(from, size);
        return compilationService.getCompilationsByAnyUser(pinned, page);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationByIdAnyUser(@PathVariable Long compId) {
        log.info("Received request to get compilation with the following param: compId={}", compId);
        return compilationService.getCompilationByIdAyUser(compId);
    }
}
