package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class NewUserRequest {

    @NotBlank
    @Size(min = 2, max = 250, message = "Проверьте длину строки")
    String name;

    @Email
    @NotNull
    @Size(min = 6, max = 254, message = "Проверьте длину строки")
    String email;
}
