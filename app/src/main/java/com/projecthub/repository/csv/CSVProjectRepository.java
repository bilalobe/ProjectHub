// package com.projecthub.repository.csv;

// import java.io.FileReader;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.List;
// import java.util.Optional;
// import java.util.function.Function;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.data.domain.Example;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
// import org.springframework.lang.NonNull;
// import org.springframework.stereotype.Repository;

// import com.opencsv.CSVReader;
// import com.opencsv.CSVWriter;
// import com.opencsv.bean.ColumnPositionMappingStrategy;
// import com.opencsv.bean.CsvToBeanBuilder;
// import com.opencsv.bean.StatefulBeanToCsv;
// import com.opencsv.bean.StatefulBeanToCsvBuilder;
// import com.opencsv.exceptions.CsvDataTypeMismatchException;
// import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
// import com.projecthub.model.Project;
// import com.projecthub.repository.custom.CustomProjectRepository;

// @Repository("csvProjectRepository")
// public class CSVProjectRepository implements CustomProjectRepository {

//     @Value("${app.projects.filepath}")
//     private String projectsFilePath;

//     @Override
//     public @NonNull List<Project> findAll() {
//         try (CSVReader reader = new CSVReader(new FileReader(projectsFilePath))) {
//             ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
//             strategy.setType(Project.class);
//             String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
//             strategy.setColumnMapping(memberFieldsToBindTo);

//             return new CsvToBeanBuilder<Project>(reader)
//                     .withMappingStrategy(strategy)
//                     .build()
//                     .parse();
//         } catch (IOException e) {
//             throw new RuntimeException("Error reading projects from CSV", e);
//         }
//     }

//     @Override
//     public List<Project> findAllByTeamId(Long teamId) {
//         return findAll().stream()
//                 .filter(project -> project.getTeam() != null && project.getTeam().getId().equals(teamId))
//                 .toList();
//     }

//     @Override
//     public Optional<Project> findProjectWithComponentsById(Long projectId) {
//         return findAll().stream()
//                 .filter(project -> project.getId().equals(projectId))
//                 .findFirst();
//     }

//     @Override
//     public @NonNull <S extends Project> S save(S project) {
//         try {
//             List<Project> projects = findAll();
//             projects.removeIf(p -> p.getId().equals(project.getId()));
//             projects.add(project);

//             try (CSVWriter writer = new CSVWriter(new FileWriter(projectsFilePath))) {
//                 ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
//                 strategy.setType(Project.class);
//                 String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
//                 strategy.setColumnMapping(memberFieldsToBindTo);

//                 StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
//                         .withMappingStrategy(strategy)
//                         .build();

//                 beanToCsv.write(projects);
//             }

//             return project;
//         } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new RuntimeException("Error saving project to CSV", e);
//         }
//     }

//     @Override
//     public void deleteById(@NonNull Long projectId) {
//         try {
//             List<Project> projects = findAll();
//             projects.removeIf(p -> p.getId().equals(projectId));

//             try (CSVWriter writer = new CSVWriter(new FileWriter(projectsFilePath))) {
//                 ColumnPositionMappingStrategy<Project> strategy = new ColumnPositionMappingStrategy<>();
//                 strategy.setType(Project.class);
//                 String[] memberFieldsToBindTo = {"id", "name", "description", "team"};
//                 strategy.setColumnMapping(memberFieldsToBindTo);

//                 StatefulBeanToCsv<Project> beanToCsv = new StatefulBeanToCsvBuilder<Project>(writer)
//                         .withMappingStrategy(strategy)
//                         .build();

//                 beanToCsv.write(projects);
//             }
//         } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
//             throw new RuntimeException("Error deleting project from CSV", e);
//         }
//     }

//     @Override
//     public void flush() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'flush'");
//     }

//     @Override
//     public <S extends Project> S saveAndFlush(S entity) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAndFlush'");
//     }

//     @Override
//     public <S extends Project> List<S> saveAllAndFlush(Iterable<S> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAllAndFlush'");
//     }

//     @Override
//     public void deleteAllInBatch(Iterable<Project> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
//     }

//     @Override
//     public void deleteAllByIdInBatch(Iterable<Long> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllByIdInBatch'");
//     }

//     @Override
//     public void deleteAllInBatch() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllInBatch'");
//     }

//     @Override
//     public Project getOne(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getOne'");
//     }

//     @Override
//     public Project getById(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getById'");
//     }

//     @Override
//     public Project getReferenceById(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getReferenceById'");
//     }

//     @Override
//     public <S extends Project> List<S> findAll(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Project> List<S> findAll(Example<S> example, Sort sort) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Project> List<S> saveAll(Iterable<S> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'saveAll'");
//     }

//     @Override
//     public List<Project> findAllById(Iterable<Long> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAllById'");
//     }

//     @Override
//     public Optional<Project> findById(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findById'");
//     }

//     @Override
//     public boolean existsById(Long id) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'existsById'");
//     }

//     @Override
//     public long count() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'count'");
//     }

//     @Override
//     public void delete(Project entity) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'delete'");
//     }

//     @Override
//     public void deleteAllById(Iterable<? extends Long> ids) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAllById'");
//     }

//     @Override
//     public void deleteAll(Iterable<? extends Project> entities) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
//     }

//     @Override
//     public void deleteAll() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
//     }

//     @Override
//     public List<Project> findAll(Sort sort) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public Page<Project> findAll(Pageable pageable) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Project> Optional<S> findOne(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findOne'");
//     }

//     @Override
//     public <S extends Project> Page<S> findAll(Example<S> example, Pageable pageable) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findAll'");
//     }

//     @Override
//     public <S extends Project> long count(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'count'");
//     }

//     @Override
//     public <S extends Project> boolean exists(Example<S> example) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'exists'");
//     }

//     @Override
//     public <S extends Project, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'findBy'");
//     }
// }