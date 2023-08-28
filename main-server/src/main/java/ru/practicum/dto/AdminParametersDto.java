package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.State;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class AdminParametersDto {

    List<Long> users;

    List<State> states;

    List<Integer> categories;

    String rangeStart;

    String rangeEnd;

    Integer from;

    Integer size;
}
