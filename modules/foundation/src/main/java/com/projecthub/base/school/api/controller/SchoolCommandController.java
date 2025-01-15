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

    public SchoolCommandController(final SchoolCommand schoolCommand) {
        this.schoolCommand = schoolCommand;
    }

    @PostMapping
    public ResponseEntity<UUID> createSchool(@Valid @RequestBody final CreateSchoolCommand command) {
        return ResponseEntity.ok(this.schoolCommand.createSchool(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSchool(@PathVariable final UUID id, @Valid @RequestBody UpdateSchoolCommand command) {
        command = new UpdateSchoolCommand(id, command.name(), command.address(), command.contact(), id);
        this.schoolCommand.updateSchool(command);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable final UUID id) {
        this.schoolCommand.deleteSchool(new DeleteSchoolCommand(id, id));
        return ResponseEntity.noContent().build();
    }
}
