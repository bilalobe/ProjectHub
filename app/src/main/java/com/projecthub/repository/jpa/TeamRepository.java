package com.projecthub.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByClassId(Long classId);
}