package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {

    Long id;

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    UserShortDto initiator;

    LocalDateTime eventDate;

    Boolean paid;

    String title;

    Long views;
}
