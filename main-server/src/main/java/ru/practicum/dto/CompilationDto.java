package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class CompilationDto {

    Long id;

    List<EventShortDto> events;

    Boolean pinned;

    String title;
}
