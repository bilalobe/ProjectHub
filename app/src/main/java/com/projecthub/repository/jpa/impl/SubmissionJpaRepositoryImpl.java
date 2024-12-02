// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Submission;
// import com.projecthub.repository.jpa.SubmissionJpaRepository;
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
// public class SubmissionJpaRepositoryImpl implements SubmissionJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Submission> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Submission s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Submission getById(@NonNull Long id) {
//         return entityManager.find(Submission.class, id);
//     }

//     @Override
//     public List<Submission> saveAll(@NonNull Iterable<Submission> entities) {
//         for (Submission submission : entities) {
//             entityManager.persist(submission);
//         }
//         return (List<Submission>) entities;
//     }

//     @Override
//     public void delete(@NonNull Submission entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Submission saveAndFlush(@NonNull Submission entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Submission> entities) {
//         for (Submission submission : entities) {
//             entityManager.remove(submission);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Submission").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Submission getOne(@NonNull Long id) {
//         return entityManager.getReference(Submission.class, id);
//     }

//     @Override
//     public <S extends Submission, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT s FROM Submission s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Submission.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Submission> entities) {
//         for (Submission submission : entities) {
//             entityManager.remove(submission);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Submission").executeUpdate();
//     }

//     @Override
//     public <S extends Submission> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT s FROM Submission s WHERE s = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Submission> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT s FROM Submission s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Submission> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT s FROM Submission s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Submission> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT s FROM Submission s ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Submission.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Submission> findAll(@NonNull Pageable pageable) {
//         List<Submission> results = entityManager.createQuery("SELECT s FROM Submission s", Submission.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Submission.class, id) != null;
//     }

//     @Override
//     public <S extends Submission> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Submission s WHERE s = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(s) FROM Submission s", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Submission> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT s FROM Submission s WHERE s = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Submission.class, id));
//         }
//     }

//     @Override
//     public List<Submission> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT s FROM Submission s WHERE s.id IN :ids", Submission.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Submission> saveAllAndFlush(@NonNull Iterable<Submission> entities) {
//         for (Submission submission : entities) {
//             entityManager.persist(submission);
//         }
//         entityManager.flush();
//         return (List<Submission>) entities;
//     }

//     @Override
//     public Submission getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Submission.class, id);
//     }

//     @Override
//     public Optional<Submission> findById(@NonNull Long id) {
//         Submission submission = entityManager.find(Submission.class, id);
//         return Optional.ofNullable(submission);
//     }
// }