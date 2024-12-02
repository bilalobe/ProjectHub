// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.School;
// import com.projecthub.repository.jpa.SchoolRepository;
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
// public class SchoolJpaRepositoryImpl implements SchoolRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<School> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM School s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public School getById(@NonNull Long id) {
//         return entityManager.find(School.class, id);
//     }

//     @Override
//     public List<School> saveAll(@NonNull Iterable<School> entities) {
//         for (School school : entities) {
//             entityManager.persist(school);
//         }
//         return (List<School>) entities;
//     }

//     @Override
//     public void delete(@NonNull School entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public School saveAndFlush(@NonNull School entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends School> entities) {
//         for (School school : entities) {
//             entityManager.remove(school);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM School").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public School getOne(@NonNull Long id) {
//         return entityManager.getReference(School.class, id);
//     }

//     @Override
//     public <S extends School, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT s FROM School s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(School.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<School> entities) {
//         for (School school : entities) {
//             entityManager.remove(school);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM School").executeUpdate();
//     }

//     @Override
//     public <S extends School> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT s FROM School s WHERE s = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends School> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT s FROM School s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends School> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT s FROM School s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<School> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT s FROM School s ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, School.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<School> findAll(@NonNull Pageable pageable) {
//         List<School> results = entityManager.createQuery("SELECT s FROM School s", School.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(School.class, id) != null;
//     }

//     @Override
//     public <S extends School> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM School s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM School s", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends School> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT s FROM School s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(School.class, id));
//         }
//     }

//     @Override
//     public List<School> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT s FROM School s WHERE s.id IN :ids", School.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<School> saveAllAndFlush(@NonNull Iterable<School> entities) {
//         for (School school : entities) {
//             entityManager.persist(school);
//         }
//         entityManager.flush();
//         return (List<School>) entities;
//     }

//     @Override
//     public School getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(School.class, id);
//     }

//     @Override
//     public Optional<School> findById(@NonNull Long id) {
//         School school = entityManager.find(School.class, id);
//         return Optional.ofNullable(school);
//     }
// }