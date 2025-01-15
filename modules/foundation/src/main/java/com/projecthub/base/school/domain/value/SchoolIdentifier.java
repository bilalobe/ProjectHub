package com.projecthub.base.school.domain.value;

import com.projecthub.base.school.domain.enums.SchoolType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record SchoolIdentifier(
    @NotBlank
    @Column(unique = true)
    String code,

    @Column(nullable = false)
    SchoolType type,

    @Column(nullable = false)
    String district
) {
    public static SchoolIdentifier of(final String code, final SchoolType type, final String district) {
        return new SchoolIdentifier(code, type, district);
    }

    public String formatted() {
        return String.format("%s-%s-%s", this.code, this.type, this.code);
    }
}
