package ru.practicum.mainService.compilation.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;
import ru.practicum.mainService.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainService.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Received request to post compilation: {}", newCompilationDto);
        CompilationDto postedCompilation = compilationService.postNewCompilation(newCompilationDto);
        log.info("Posted compilation={}", postedCompilation);
        return postedCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Received request to delete compilation with id={}", compId);
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest request) {
        log.info("Received request to update compilation with following params: compId={}, request={}", compId, request);
        return compilationService.updateCompilation(compId, request);
    }
}
