package com.projecthub.base.school.domain.value;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Embeddable
public record SchoolContact(
    @NotBlank @Email String email,
    @NotBlank String phone,
    String website
) {

    public static SchoolContact from(SchoolContact other) {
        return new SchoolContact(
            other.email(),
            other.phone(),
            other.website()
        );
    }

    public static SchoolContact of(String email, String phone, String website) {
        return new SchoolContact(email, phone, website);
    }
}
