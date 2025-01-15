package com.projecthub.base.auth.domain.entity;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.domain.enums.security.RoleType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleType name;

    @Column
    private String description;

    public RoleType getRoleType() {
        return name;
    }

}
