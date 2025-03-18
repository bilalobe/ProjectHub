package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.student.domain.entity.Student;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class StudentSynchronizer extends BaseSynchronizer<Student> {

    public StudentSynchronizer(final LocalDataService localDataService,
                             final RemoteDataService remoteDataService,
                             final UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
    }

    @Override
    public Class<Student> getEntityType() {
        return Student.class;
    }

    @Override
    public String getEntityName() {
        return "students";
    }

    @Override
    protected List<Student> merge(final List<Student> local, final List<Student> remote) {
        return remote.stream()
            .map(remoteStudent -> {
                var localStudent = findMatchingStudent(local, remoteStudent);
                if (localStudent.isPresent()) {
                    return shouldUseLocalVersion(localStudent.get().getUpdatedAt(), remoteStudent.getUpdatedAt())
                        ? localStudent.get()
                        : remoteStudent;
                }
                return remoteStudent;
            })
            .collect(Collectors.toList());
    }

    private static java.util.Optional<Student> findMatchingStudent(Collection<Student> local, Student remote) {
        return local.stream()
            .filter(l -> Objects.equals(l.getStudentId(), remote.getStudentId()))
            .findFirst();
    }

    private static boolean shouldUseLocalVersion(ChronoLocalDateTime<LocalDate> localModified, ChronoLocalDateTime<LocalDate> remoteModified) {
        if (localModified == null) return false;
        if (remoteModified == null) return true;
        return localModified.isAfter(remoteModified);
    }
}
