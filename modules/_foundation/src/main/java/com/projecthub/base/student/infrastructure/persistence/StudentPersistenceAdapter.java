package com.projecthub.base.student.infrastructure.persistence;


import com.projecthub.base.student.api.dto.StudentDTO;
import com.projecthub.base.student.api.mapper.StudentMapper;
import com.projecthub.base.student.application.port.out.StudentPort;
import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
class StudentPersistenceAdapter implements StudentPort {
    private final StudentJpaRepository repository;
    private final StudentMapper mapper;

    public StudentPersistenceAdapter(final StudentJpaRepository repository, final StudentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public StudentDTO save(final StudentDTO dto) {
        final var entity = this.mapper.toEntity(dto);
        return this.mapper.toDto(this.repository.save(entity));
    }

    @Override
    public Optional<StudentDTO> findById(final UUID id) {
        return this.repository.findById(id)
            .map(this.mapper::toDto);
    }

    @Override
    public void delete(final UUID id) {
        this.repository.deleteById(id);
    }
}
