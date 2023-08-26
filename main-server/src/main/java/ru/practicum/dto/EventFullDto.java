package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Location;
import ru.practicum.model.State;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventFullDto {

    Long id;

    String annotation;

    CategoryDto category;

    Integer confirmedRequests;

    String description;

    LocalDateTime createdOn;

    LocalDateTime publishedOn;

    UserShortDto initiator;

    String eventDate;

    Location location;

    Boolean paid;

    Integer participantLimit;

    Boolean requestModeration;

    String title;

    State state;

    Long views;
}
