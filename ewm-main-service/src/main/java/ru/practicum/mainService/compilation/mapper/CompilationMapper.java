package ru.practicum.mainService.compilation.mapper;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;
import ru.practicum.mainService.event.mapper.EventMapper;
import ru.practicum.mainService.compilation.model.Compilation;
import ru.practicum.mainService.event.model.Event;

import java.util.HashSet;
import java.util.Set;

public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        if (!events.isEmpty()) {
            compilation.setEvents(events);
        }
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }

    public static CompilationDto compilationDto(Compilation compilation) {
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        if (compilation.getEvents() == null) {
            dto.setEvents(new HashSet<>());
        } else {
            dto.setEvents(EventMapper.toEventShortDtoSet(compilation.getEvents()));
        }
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());

        return dto;
    }
}
