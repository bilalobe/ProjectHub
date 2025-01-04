package com.projecthub.base.student.infrastructure.persistence;


import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.application.port.StudentPort;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class StudentPersistenceAdapter implements StudentPort {
    private final StudentJpaRepository repository;
    private final StudentMapper mapper;

    public StudentPersistenceAdapter(StudentJpaRepository repository, StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public StudentDTO save(StudentDTO dto) {
        var entity = mapper.toEntity(dto);
        return mapper.toDto(repository.save(entity));
    }

    @Override
    public Optional<StudentDTO> findById(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public void delete(UUID id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    // ...existing code...
}
