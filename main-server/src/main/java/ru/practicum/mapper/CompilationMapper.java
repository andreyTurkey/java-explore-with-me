package ru.practicum.mapper;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.Compilation;

import java.util.List;

public class CompilationMapper {

    public static Compilation getCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = Compilation.builder()
                .events(newCompilationDto.getEvents())
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
        return compilation;
    }

    public static CompilationDto getCompilationDto(Compilation compilation, List<EventShortDto> events) {
        CompilationDto compilationDto = CompilationDto.builder()
                .events(events)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
        return compilationDto;
    }
}
