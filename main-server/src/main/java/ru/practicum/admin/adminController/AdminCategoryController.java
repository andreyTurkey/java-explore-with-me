package ru.practicum.admin.adminController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/admin/categories")
@AllArgsConstructor
public class AdminCategoryController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> addCategory(@Valid @RequestBody NewCategoryDto body) {
        log.debug("Запрос на добавление новой категории {}", body);
        return  new ResponseEntity<>(adminService.addCategory(body), HttpStatus.CREATED);
    }

    @PatchMapping(value = "{catId}")
    public ResponseEntity<Object> changeCategory(@Valid @RequestBody CategoryDto body,
                                                 @Positive @PathVariable("catId") Integer catId) {
        log.debug("Запрос на изменение категории {} catId = {}", body, catId);
        return  new ResponseEntity<>(adminService.changeCategory(catId, body), HttpStatus.OK);
    }

    @DeleteMapping(value = "{catId}")
    public ResponseEntity<Objects> deleteCategory(@Positive @PathVariable("catId") Integer catId) {
        log.debug("Запрос на удаление категории catId = {}", catId);
        adminService.deleteCategory(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
