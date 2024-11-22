package com.projecthub.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.projecthub.model.Project;
import com.projecthub.model.Student;
import com.projecthub.model.Submission;
import com.projecthub.model.Team;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.SchoolSummary;
import com.projecthub.dto.StudentSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.model.AppUser;
import com.projecthub.service.ProjectService;
import com.projecthub.service.StudentService;
import com.projecthub.service.TeamService;
import com.projecthub.service.SchoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CSVHandler {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private SchoolService schoolService;

    public List<Submission> readSubmissionsFromCSV(String filePath) throws IOException {
        List<Submission> submissions = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Submission> csvSubmissions = new CsvToBeanBuilder<Submission>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            for (Submission csvSubmission : csvSubmissions) {
                Project project = projectService.getProjectById(csvSubmission.getProject().getId())
                        .map(ProjectSummary::toProject)
                        .orElse(null);
                StudentSummary studentSummary = studentService.getStudentSummaryById(csvSubmission.getStudent().getId());
                Student student = Optional.ofNullable(studentSummary)
                        .map(StudentSummary::toStudent)
                        .orElse(null);
                Submission submission = new Submission(
                    csvSubmission.getId(),
                    student, // Pass the Student object
                    project, // Pass the Project object
                    csvSubmission.getContent(),
                    filePath, csvSubmission.getGrade()
                );
                submissions.add(submission);
            }
        } catch (IOException e) {
            throw new IOException("Error reading submissions from CSV", e);
        }
        return submissions;
    }

    public void writeSubmissionsToCSV(List<Submission> submissions, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Submission> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Submission.class);
            String[] memberFieldsToBindTo = {"id", "projectId", "studentId", "content", "grade"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            StatefulBeanToCsv<Submission> beanToCsv = new StatefulBeanToCsvBuilder<Submission>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(submissions);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IOException("Error writing submissions to CSV", e);
        }
    }

    public List<Project> readProjectsFromCSV(String filePath) throws IOException {
        List<Project> projects = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Project> csvProjects = new CsvToBeanBuilder<Project>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            for (Project csvProject : csvProjects) {
                Project project = new Project();
                project.setId(csvProject.getId());
                project.setName(csvProject.getName());
                project.setDescription(csvProject.getDescription());
                // Set team based on ID
                project.setTeam(teamService.getTeamById(csvProject.getTeam().getId())
                        .map(TeamSummary::toTeam)
                        .orElse(null));
                projects.add(project);
            }
        } catch (IOException e) {
            throw new IOException("Error reading projects from CSV", e);
        }
        return projects;
    }

    public void writeProjectsToCSV(List<Project> projects, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Project.class);
            String[] memberFieldsToBindTo = {"id", "name", "description", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(projects);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IOException("Error writing projects to CSV", e);
        }
    }

    public List<Student> readStudentsFromCSV(String filePath) throws IOException {
        List<Student> students = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Student> csvStudents = new CsvToBeanBuilder<Student>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            for (Student csvStudent : csvStudents) {
                Student student = new Student();
                student.setId(csvStudent.getId());
                student.setName(csvStudent.getName());
                students.add(student);
            }
        } catch (IOException e) {
            throw new IOException("Error reading students from CSV", e);
        }
        return students;
    }

    public void writeStudentsToCSV(List<Student> students, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Student> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Student.class);
            String[] memberFieldsToBindTo = {"id", "name"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            StatefulBeanToCsv<Student> beanToCsv = new StatefulBeanToCsvBuilder<Student>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(students);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IOException("Error writing students to CSV", e);
        }
    }

    public List<Team> readTeamsFromCSV(String filePath) throws IOException {
        List<Team> teams = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<Team> csvTeams = new CsvToBeanBuilder<Team>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            for (Team csvTeam : csvTeams) {
                Team team = new Team();
                team.setId(csvTeam.getId());
                team.setName(csvTeam.getName());
                // Set school based on ID
                team.setSchool(schoolService.getSchoolById(csvTeam.getSchool().getId())
                        .map(SchoolSummary::toSchool)
                        .orElse(null));
                teams.add(team);
            }
        } catch (IOException e) {
            throw new IOException("Error reading teams from CSV", e);
        }
        return teams;
    }

    public void writeTeamsToCSV(List<Team> teams, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<Team> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Team.class);
            String[] memberFieldsToBindTo = {"id", "name", "schoolId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            StatefulBeanToCsv<Team> beanToCsv = new StatefulBeanToCsvBuilder<Team>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(teams);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IOException("Error writing teams to CSV", e);
        }
    }

    public List<AppUser> readUsersFromCSV(String filePath) throws IOException {
        List<AppUser> users = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(AppUser.class);
            String[] memberFieldsToBindTo = {"id", "username", "password", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<AppUser> csvUsers = new CsvToBeanBuilder<AppUser>(reader)
                    .withMappingStrategy(strategy)
                    .build()
                    .parse();

            for (AppUser csvUser : csvUsers) {
                AppUser user = new AppUser();
                user.setId(csvUser.getId());
                user.setUsername(csvUser.getUsername());
                user.setPassword(csvUser.getPassword());
                // Set team based on ID
                user.setTeam(teamService.getTeamById(csvUser.getTeam().getId())
                        .map(TeamSummary::toTeam)
                        .orElse(null));
                users.add(user);
            }
        } catch (IOException e) {
            throw new IOException("Error reading users from CSV", e);
        }
        return users;
    }

    public void writeUsersToCSV(List<AppUser> users, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            ColumnPositionMappingStrategy<AppUser> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(AppUser.class);
            String[] memberFieldsToBindTo = {"id", "username", "password", "teamId"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            StatefulBeanToCsv<AppUser> beanToCsv = new StatefulBeanToCsvBuilder<AppUser>(writer)
                    .withMappingStrategy(strategy)
                    .build();

            beanToCsv.write(users);
        } catch (CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            throw new IOException("Error writing users to CSV", e);
        }
    }
}