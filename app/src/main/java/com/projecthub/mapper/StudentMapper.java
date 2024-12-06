package com.projecthub.mapper;

import com.projecthub.dto.StudentDTO;
import com.projecthub.model.Student;
import com.projecthub.model.Team;
import com.projecthub.repository.TeamRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class StudentMapper {

    private TeamRepository teamRepository;

    @Mapping(source = "teamId", target = "team", qualifiedByName = "mapTeamIdToTeam")
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    public abstract Student toStudent(StudentDTO studentDTO);

    @Mapping(source = "team.id", target = "teamId")
    public abstract StudentDTO toStudentDTO(Student student);

    @Mapping(source = "teamId", target = "team", qualifiedByName = "mapTeamIdToTeam")
    @Mapping(target = "deleted", ignore = true) // Assuming deleted is managed separately
    public abstract void updateStudentFromDTO(StudentDTO studentDTO, @MappingTarget Student student);

    @Named("mapTeamIdToTeam")
    protected Team mapTeamIdToTeam(UUID teamId) {
        return teamRepository.findById(teamId).orElseThrow(() -> new IllegalArgumentException("Invalid team ID: " + teamId));
    }

    @Named("mapTeamToTeamId")
    protected UUID mapTeamToTeamId(Team team) {
        return team != null ? team.getId() : null;
    }
}