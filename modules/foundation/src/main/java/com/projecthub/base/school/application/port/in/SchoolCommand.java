package com.projecthub.base.school.application.port.in;

import com.projecthub.base.school.application.port.in.command.ArchiveSchoolCommand;
import com.projecthub.base.school.application.port.in.command.CreateSchoolCommand;
import com.projecthub.base.school.application.port.in.command.DeleteSchoolCommand;
import com.projecthub.base.school.application.port.in.command.UpdateSchoolCommand;

import java.util.UUID;

public interface SchoolCommand {
    UUID createSchool(CreateSchoolCommand command);

    void updateSchool(UpdateSchoolCommand command);

    void deleteSchool(DeleteSchoolCommand command);

    void archiveSchool(ArchiveSchoolCommand command);
}
