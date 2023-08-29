package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateParticipationAnswer {

    Long id;

    LocalDateTime created;

    Long event;

    Long requester;

    State status;
}
