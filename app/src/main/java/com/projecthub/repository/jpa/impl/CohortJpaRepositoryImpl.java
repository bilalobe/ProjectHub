// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Cohort;
// import com.projecthub.repository.jpa.CohortJpaRepository;
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
// public class CohortJpaRepositoryImpl implements CohortJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Cohort> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Cohort c WHERE c = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Cohort getById(@NonNull Long id) {
//         return entityManager.find(Cohort.class, id);
//     }

//     @Override
//     public List<Cohort> saveAll(@NonNull Iterable<Cohort> entities) {
//         for (Cohort cohort : entities) {
//             entityManager.persist(cohort);
//         }
//         return (List<Cohort>) entities;
//     }

//     @Override
//     public void delete(@NonNull Cohort entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Cohort saveAndFlush(@NonNull Cohort entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Cohort> entities) {
//         for (Cohort cohort : entities) {
//             entityManager.remove(cohort);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Cohort").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Cohort getOne(@NonNull Long id) {
//         return entityManager.getReference(Cohort.class, id);
//     }

//     @Override
//     public <S extends Cohort, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT c FROM Cohort c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Cohort.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Cohort> entities) {
//         for (Cohort cohort : entities) {
//             entityManager.remove(cohort);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Cohort").executeUpdate();
//     }

//     @Override
//     public <S extends Cohort> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT c FROM Cohort c WHERE c = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Cohort> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT c FROM Cohort c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Cohort> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT c FROM Cohort c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Cohort> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT c FROM Cohort c ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Cohort.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Cohort> findAll(@NonNull Pageable pageable) {
//         List<Cohort> results = entityManager.createQuery("SELECT c FROM Cohort c", Cohort.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Cohort.class, id) != null;
//     }

//     @Override
//     public <S extends Cohort> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Cohort c WHERE c = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Cohort c", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Cohort> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT c FROM Cohort c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Cohort.class, id));
//         }
//     }

//     @Override
//     public List<Cohort> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT c FROM Cohort c WHERE c.id IN :ids", Cohort.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Cohort> saveAllAndFlush(@NonNull Iterable<Cohort> entities) {
//         for (Cohort cohort : entities) {
//             entityManager.persist(cohort);
//         }
//         entityManager.flush();
//         return (List<Cohort>) entities;
//     }

//     @Override
//     public Cohort getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Cohort.class, id);
//     }

//     @Override
//     public Optional<Cohort> findById(@NonNull Long id) {
//         Cohort cohort = entityManager.find(Cohort.class, id);
//         return Optional.ofNullable(cohort);
//     }

//     @Override
//     public List<Cohort> findBySchoolId(@NonNull Long schoolId) {
//         return entityManager.createQuery("SELECT c FROM Cohort c WHERE c.school.id = :schoolId", Cohort.class)
//                 .setParameter("schoolId", schoolId)
//                 .getResultList();
//     }
// }