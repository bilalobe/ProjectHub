package com.projecthub.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projecthub.model.Team;
import com.projecthub.repository.custom.CustomTeamRepository;

@Repository("postgresTeamRepository")
public interface TeamRepository extends JpaRepository<Team, Long>, CustomTeamRepository {
    // Custom query methods can be added here
}