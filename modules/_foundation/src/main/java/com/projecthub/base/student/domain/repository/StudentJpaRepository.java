package com.projecthub.base.student.domain.repository;

import com.projecthub.base.shared.domain.enums.status.ActivationStatus;
import com.projecthub.base.student.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentJpaRepository extends JpaRepository<Student, UUID>, JpaSpecificationExecutor<Student> {
    List<Student> findByTeamId(UUID teamId);
    List<Student> findByStatus(ActivationStatus status);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByStudentId(UUID studentId);
    
    @Query("SELECT s FROM Student s WHERE s.team.id = :teamId AND s.status = :status")
    List<Student> findByTeamIdAndStatus(UUID teamId, ActivationStatus status);
    
    boolean existsByEmail(String email);
    boolean existsByStudentId(UUID studentId);
}
