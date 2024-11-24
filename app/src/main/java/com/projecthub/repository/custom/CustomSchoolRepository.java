package com.projecthub.repository.custom;

import com.projecthub.dto.SchoolSummary;
import com.projecthub.model.School;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

public interface CustomSchoolRepository extends CrudRepository<School, Long> {

    @Override
    void deleteById(@NonNull Long id);

    void save(SchoolSummary schoolSummary);
}