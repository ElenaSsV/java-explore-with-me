package ru.practicum.mainService.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;
import ru.practicum.mainService.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto postNewCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest request);

    List<CompilationDto> getCompilationsByAnyUser(Boolean pinned, PageRequest page);

    CompilationDto getCompilationByIdAyUser(long compId);
}
