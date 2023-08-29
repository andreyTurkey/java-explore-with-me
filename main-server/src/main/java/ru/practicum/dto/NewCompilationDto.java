package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@ToString
public class NewCompilationDto {

    List<Long> events;

    Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50, message = "Проверьте длину строки")
    String title;
}
