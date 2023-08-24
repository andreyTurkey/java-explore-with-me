package ru.practicum.admin.service;

import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.UpdateCompilationRequest;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    CompilationDto changeCompilation(Long compId, UpdateCompilationRequest ucr);

    void deleteCompilation(Long compId);
}
