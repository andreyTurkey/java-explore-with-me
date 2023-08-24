package ru.practicum.admin.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.UpdateCompilationRequest;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationServiceImpl implements CompilationService {

    EventRepository eventRepository;

    CompilationRepository compilationRepository;


    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        compilationRepository.save(CompilationMapper.getCompilation(newCompilationDto));
        List<EventShortDto> eventShortDtos;
        if (newCompilationDto.getEvents() == null) {
            eventShortDtos = new ArrayList<>();
        } else {
            eventShortDtos = eventRepository.findAllByIdIn(newCompilationDto.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        }
        return CompilationMapper.getCompilationDto(compilationRepository.findAllByTitle(newCompilationDto.getTitle()), eventShortDtos);
    }

    @Override
    @Transactional
    public CompilationDto changeCompilation(Long compId, UpdateCompilationRequest ucr) {
        Compilation oldCompilation = compilationRepository.getReferenceById(compId);
        List<EventShortDto> eventShortDtos;
        if (ucr.getEvents() != null) {
            oldCompilation.setEvents(ucr.getEvents());
            eventShortDtos = eventRepository.findAllByIdIn(ucr.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        } else {
            eventShortDtos = eventRepository.findAllByIdIn(oldCompilation.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        }
        if (ucr.getPinned() != null) {
            oldCompilation.setPinned(ucr.getPinned());
        }
        if (ucr.getTitle() != null) {
            oldCompilation.setTitle(ucr.getTitle());
        }
        compilationRepository.save(oldCompilation);
        return CompilationMapper.getCompilationDto(oldCompilation, eventShortDtos);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                "Подборка ID = " + compId + " не найдена."));
        compilationRepository.deleteById(compId);
    }
}
