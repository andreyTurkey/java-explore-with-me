package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class CompilationPublicDto {

    Long id;

    List<EventShortDto> events;

    Boolean pinned;

    String title;
}
