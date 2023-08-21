package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserShortDto {

    Long id;

    String name;
}
