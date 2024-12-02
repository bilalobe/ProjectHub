// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Student;
// import com.projecthub.repository.jpa.StudentJpaRepository;
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
// public class StudentJpaRepositoryImpl implements StudentJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Student> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Student s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Student getById(@NonNull Long id) {
//         return entityManager.find(Student.class, id);
//     }

//     @Override
//     public List<Student> saveAll(@NonNull Iterable<Student> entities) {
//         for (Student student : entities) {
//             entityManager.persist(student);
//         }
//         return (List<Student>) entities;
//     }

//     @Override
//     public void delete(@NonNull Student entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Student saveAndFlush(@NonNull Student entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Student> entities) {
//         for (Student student : entities) {
//             entityManager.remove(student);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Student").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Student getOne(@NonNull Long id) {
//         return entityManager.getReference(Student.class, id);
//     }

//     @Override
//     public <S extends Student, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT s FROM Student s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Student.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Student> entities) {
//         for (Student student : entities) {
//             entityManager.remove(student);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Student").executeUpdate();
//     }

//     @Override
//     public <S extends Student> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT s FROM Student s WHERE s = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Student> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT s FROM Student s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Student> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT s FROM Student s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Student> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT s FROM Student s ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Student.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Student> findAll(@NonNull Pageable pageable) {
//         List<Student> results = entityManager.createQuery("SELECT s FROM Student s", Student.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Student.class, id) != null;
//     }

//     @Override
//     public <S extends Student> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Student s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Student s", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Student> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT s FROM Student s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Student.class, id));
//         }
//     }

//     @Override
//     public List<Student> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT s FROM Student s WHERE s.id IN :ids", Student.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Student> saveAllAndFlush(@NonNull Iterable<Student> entities) {
//         for (Student student : entities) {
//             entityManager.persist(student);
//         }
//         entityManager.flush();
//         return (List<Student>) entities;
//     }

//     @Override
//     public Student getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Student.class, id);
//     }

//     @Override
//     public Optional<Student> findById(@NonNull Long id) {
//         Student student = entityManager.find(Student.class, id);
//         return Optional.ofNullable(student);
//     }

//     @Override
//     public Optional<Student> findByUsername(@NonNull String username) {
//         return entityManager.createQuery("SELECT s FROM Student s WHERE s.username = :username", Student.class)
//                 .setParameter("username", username)
//                 .getResultList()
//                 .stream()
//                 .findFirst();
//     }

//     @Override
//     public List<Student> findByTeamId(@NonNull Long teamId) {
//         return entityManager.createQuery("SELECT s FROM Student s WHERE s.team.id = :teamId", Student.class)
//                 .setParameter("teamId", teamId)
//                 .getResultList();
//     }
// }