package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@ToString
public class CategoryDto {

    Integer id;

    @Size(min = 1, max = 50, message = "Проверьте длину имени")
    String name;
}
