package ru.practicum.dto;

import lombok.*;
import ru.practicum.model.Location;
import ru.practicum.model.State;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewEventDto {

    @NotBlank(message = "ошибка по полю annotation")
    @Size(min = 20, max = 2000, message = "Проверьте длину строки")
    String annotation;

    @NotNull(message = "ошибка по полю categoryId")
    Integer category;

    Integer confirmedRequests;

    @NotBlank(message = "ошибка по полю description")
    @Size(min = 20, max = 7000, message = "Проверьте длину строки")
    String description;

    @NotBlank(message = "ошибка по полю eventDate")
    String eventDate;

    @NotNull(message = "ошибка по полю location")
    Location location;

    Boolean paid;

    @Min(value = 0)
    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Проверьте длину строки")
    String title;

    State state;

    Boolean available;
}
