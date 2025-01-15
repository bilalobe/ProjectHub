package com.projecthub.base.student.api;

import com.projecthub.base.student.api.dto.StudentDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class StudentApiController {
    private final StudentApplicationService service;

    public StudentApiController(final StudentApplicationService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<StudentDTO> create(@Valid @RequestBody final StudentCommand.Create command) {
        return ResponseEntity.ok(this.service.handle(command));
    }

}
