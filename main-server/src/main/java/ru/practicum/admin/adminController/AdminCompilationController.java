package ru.practicum.admin.adminController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.NewCompilationDto;
import ru.practicum.model.UpdateCompilationRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(path = "/admin/compilations")
@AllArgsConstructor
public class AdminCompilationController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<Object> addCompilation(@Valid @RequestBody NewCompilationDto body) {
        log.debug("Запрос на добавление новой подборки {}", body);
        return new ResponseEntity<>(adminService.addCompilation(body), HttpStatus.CREATED);
    }

    @PatchMapping(value = "{compId}")
    public ResponseEntity<Object> changeCompilation(@Valid @RequestBody UpdateCompilationRequest body,
                                                    @Positive @PathVariable("compId") Long compId) {
        log.debug("Запрос на изменение подборки {}", body);
        return new ResponseEntity<>(adminService.changeCompilation(compId, body), HttpStatus.OK);
    }

    @DeleteMapping(value = "{compId}")
    public ResponseEntity<Objects> deleteCompilation(@Positive @PathVariable("compId") Long compId) {
        adminService.deleteCompilation(compId);
        log.debug("Запрос на удаление подборки ID = {}", compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
