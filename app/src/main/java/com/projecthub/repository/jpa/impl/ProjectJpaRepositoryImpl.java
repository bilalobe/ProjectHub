// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Project;
// import com.projecthub.repository.jpa.ProjectJpaRepository;
// import jakarta.persistence.EntityManager;
// import jakarta.persistence.PersistenceContext;
// import org.springframework.data.domain.*;
// import org.springframework.data.repository.query.FluentQuery;
// import org.springframework.lang.NonNull;
// import org.springframework.stereotype.Repository;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.function.Function;

// @Repository
// @Transactional
// public class ProjectJpaRepositoryImpl implements ProjectJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Project> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(p) FROM Project p WHERE p = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Project getById(@NonNull Long id) {
//         return entityManager.find(Project.class, id);
//     }

//     @Override
//     public List<Project> saveAll(@NonNull Iterable<Project> entities) {
//         for (Project project : entities) {
//             entityManager.persist(project);
//         }
//         return (List<Project>) entities;
//     }

//     @Override
//     public void delete(@NonNull Project entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Project saveAndFlush(@NonNull Project entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Project> entities) {
//         for (Project project : entities) {
//             entityManager.remove(project);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Project").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Project getOne(@NonNull Long id) {
//         return entityManager.getReference(Project.class, id);
//     }

//     @Override
//     public <S extends Project, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT p FROM Project p WHERE p = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Project.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Project> entities) {
//         for (Project project : entities) {
//             entityManager.remove(project);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Project").executeUpdate();
//     }

//     @Override
//     public <S extends Project> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT p FROM Project p WHERE p = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Project> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT p FROM Project p WHERE p = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Project> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT p FROM Project p WHERE p = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Project> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT p FROM Project p ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Project.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Project> findAll(@NonNull Pageable pageable) {
//         List<Project> results = entityManager.createQuery("SELECT p FROM Project p", Project.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Project.class, id) != null;
//     }

//     @Override
//     public <S extends Project> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(p) FROM Project p WHERE p = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(p) FROM Project p", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Project> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT p FROM Project p WHERE p = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Project.class, id));
//         }
//     }

//     @Override
//     public List<Project> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT p FROM Project p WHERE p.id IN :ids", Project.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Project> saveAllAndFlush(@NonNull Iterable<Project> entities) {
//         for (Project project : entities) {
//             entityManager.persist(project);
//         }
//         entityManager.flush();
//         return (List<Project>) entities;
//     }

//     @Override
//     public Project getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Project.class, id);
//     }

//     @Override
//     public Optional<Project> findById(@NonNull Long id) {
//         Project project = entityManager.find(Project.class, id);
//         return Optional.ofNullable(project);
//     }

//     @Override
//     public List<Project> findAllByTeamId(@NonNull Long teamId) {
//         return entityManager.createQuery("SELECT p FROM Project p WHERE p.team.id = :teamId", Project.class)
//                 .setParameter("teamId", teamId)
//                 .getResultList();
//     }

//     @Override
//     public Optional<Project> findProjectWithComponentsById(@NonNull Long projectId) {
//         return entityManager.createQuery("SELECT p FROM Project p JOIN FETCH p.components WHERE p.id = :projectId", Project.class)
//                 .setParameter("projectId", projectId)
//                 .getResultList()
//                 .stream()
//                 .findFirst();
//     }
// }