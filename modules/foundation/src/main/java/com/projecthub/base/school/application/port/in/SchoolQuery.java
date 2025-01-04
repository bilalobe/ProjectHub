package com.projecthub.base.school.application.port.in;

import com.projecthub.base.school.api.dto.SchoolDTO;
import com.projecthub.base.school.domain.criteria.SchoolSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SchoolQuery {
    SchoolDTO getSchoolById(UUID id);

    Page<SchoolDTO> getAllSchools(PageRequest pageRequest);

    Page<SchoolDTO> searchSchools(SchoolSearchCriteria criteria, Pageable pageable);

    Page<SchoolDTO> getActiveSchools(Pageable pageable);

    Page<SchoolDTO> getArchivedSchools(Pageable pageable);
}
