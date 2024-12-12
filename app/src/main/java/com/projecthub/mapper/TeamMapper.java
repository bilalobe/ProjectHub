package com.projecthub.mapper;

import com.projecthub.dto.TeamDTO;
import com.projecthub.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "students", ignore = true)
    Team toTeam(TeamDTO teamDTO);

    TeamDTO toTeamDTO(Team team);

    @Mapping(target = "students", ignore = true)
    void updateTeamFromDTO(TeamDTO teamDTO, @MappingTarget Team team);
}