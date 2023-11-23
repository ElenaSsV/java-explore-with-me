package ru.practicum.mainService.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.mapper.CompilationMapper;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;
import ru.practicum.mainService.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainService.compilation.model.Compilation;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.exception.DataConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.compilation.repository.CompilationRepository;
import ru.practicum.mainService.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto postNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Posting new compilation={}", newCompilationDto);
        Set<Event> eventsInCompilation = new HashSet<>();

        if (newCompilationDto.getEvents() != null) {
            eventsInCompilation = new HashSet<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        }
        Compilation compilationToPost = CompilationMapper.toCompilation(newCompilationDto, eventsInCompilation);
        log.info("Compilation to post: {}", compilationToPost);
        Compilation savedCompilation;

        try {
            savedCompilation = compilationRepository.save(compilationToPost);
            log.info("Saved compilation: {}", savedCompilation);

        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("Compilation cannot be saved. " + e.getMessage());
        }
        return CompilationMapper.compilationDto(savedCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        log.info("Deleting compilation with id={}", compId);
        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " is not found.");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest updateRequest) {
        log.info("Updating compilation with id={} to {}", compId, updateRequest);

        Compilation compilationToUpdate = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " is not found."));

        Set<Event> eventsToCompilation;
        if (updateRequest.getEvents() != null) {
            eventsToCompilation = new HashSet<>(eventRepository.findAllById(updateRequest.getEvents()));
            log.info("Setting events to {}", eventsToCompilation);
            compilationToUpdate.setEvents(eventsToCompilation);
        }

        if (updateRequest.getPinned() != null) {
            log.info("Setting pinned to {}", updateRequest.getPinned());
            compilationToUpdate.setPinned(updateRequest.getPinned());
        }

        if (updateRequest.getTitle() != null) {
            log.info("Setting title to {}", updateRequest.getTitle());
            compilationToUpdate.setTitle(updateRequest.getTitle());
        }
        return CompilationMapper.compilationDto(compilationRepository.save(compilationToUpdate));
    }

    @Override
    public List<CompilationDto> getCompilationsByAnyUser(Boolean pinned, PageRequest pageRequest) {
        log.info("Retrieving compilations with pinned={} via public API, pageRequest={}", pinned, pageRequest);

        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageRequest).stream()
                    .map(CompilationMapper::compilationDto)
                    .collect(Collectors.toList());

        } else {
            return compilationRepository.findAll(pageRequest).stream()
                    .map(CompilationMapper::compilationDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CompilationDto getCompilationByIdAyUser(long compId) {
        log.info("Retrieving compilation with id={} via public API", compId);

        Compilation searchedComp = compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException("Compilation with id=" + compId + " is not found"));

        return CompilationMapper.compilationDto(searchedComp);
    }


}
