package com.projecthub.base.school.api.controller;

import com.projecthub.base.school.application.port.in.SchoolCommand;
import com.projecthub.base.school.application.port.in.command.CreateSchoolCommand;
import com.projecthub.base.school.application.port.in.command.DeleteSchoolCommand;
import com.projecthub.base.school.application.port.in.command.UpdateSchoolCommand;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schools/commands")
public class SchoolCommandController {
    private final SchoolCommand schoolCommand;

    public SchoolCommandController(SchoolCommand schoolCommand) {
        this.schoolCommand = schoolCommand;
    }

    @PostMapping
    public ResponseEntity<UUID> createSchool(@Valid @RequestBody CreateSchoolCommand command) {
        return ResponseEntity.ok(schoolCommand.createSchool(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSchool(@PathVariable UUID id, @Valid @RequestBody UpdateSchoolCommand command) {
        command = new UpdateSchoolCommand(id, command.name(), command.address(), command.contact(), id);
        schoolCommand.updateSchool(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable UUID id) {
        schoolCommand.deleteSchool(new DeleteSchoolCommand(id, id));
        return ResponseEntity.noContent().build();
    }
}
