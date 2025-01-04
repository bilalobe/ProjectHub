package com.projecthub.base.school.domain.validation;

import com.projecthub.base.school.domain.entity.School;
import jakarta.validation.constraints.NotNull;

public interface SchoolValidation {
    void validateCreate(@NotNull School school);

    void validateUpdate(@NotNull School school);

    void validateArchive(@NotNull School school);

    void validateDelete(@NotNull School school);
}
