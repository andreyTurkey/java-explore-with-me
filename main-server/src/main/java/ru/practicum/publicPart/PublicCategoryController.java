package ru.practicum.publicPart;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/categories")
@AllArgsConstructor
public class PublicCategoryController {

    private final Service service;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос на получение всех категорий.");
        return service.getCategories(from, size);
    }

    @GetMapping(value = "{catId}")
    public CategoryDto getCategoriesById(@Positive @PathVariable("catId") Integer catId) {
        log.debug("Запрос на получение  категории ID ={}", catId);
        return service.getCategoriesById(catId);
    }
}
