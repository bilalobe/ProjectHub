package com.projecthub.base.student.application.port;


import com.projecthub.base.student.api.dto.StudentDTO;

import java.util.Optional;
import java.util.UUID;

public interface StudentPort {
    StudentDTO save(StudentDTO student);

    Optional<StudentDTO> findById(UUID id);

    void delete(UUID id);
}
