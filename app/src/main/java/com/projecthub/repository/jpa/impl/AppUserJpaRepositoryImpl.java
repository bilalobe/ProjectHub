// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.AppUser;
// import com.projecthub.repository.jpa.UserJpaRepository;
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
// public class UserJpaRepositoryImpl implements UserJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<AppUser> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(u) FROM AppUser u WHERE u = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public AppUser getById(@NonNull Long id) {
//         return entityManager.find(AppUser.class, id);
//     }

//     @Override
//     public List<AppUser> saveAll(@NonNull Iterable<AppUser> entities) {
//         for (AppUser user : entities) {
//             entityManager.persist(user);
//         }
//         return (List<AppUser>) entities;
//     }

//     @Override
//     public void delete(@NonNull AppUser entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public AppUser saveAndFlush(@NonNull AppUser entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends AppUser> entities) {
//         for (AppUser user : entities) {
//             entityManager.remove(user);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM AppUser").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public AppUser getOne(@NonNull Long id) {
//         return entityManager.getReference(AppUser.class, id);
//     }

//     @Override
//     public <S extends AppUser, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT u FROM AppUser u WHERE u = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(AppUser.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<AppUser> entities) {
//         for (AppUser user : entities) {
//             entityManager.remove(user);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM AppUser").executeUpdate();
//     }

//     @Override
//     public <S extends AppUser> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT u FROM AppUser u WHERE u = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends AppUser> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT u FROM AppUser u WHERE u = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends AppUser> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT u FROM AppUser u WHERE u = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<AppUser> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT u FROM AppUser u ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, AppUser.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<AppUser> findAll(@NonNull Pageable pageable) {
//         List<AppUser> results = entityManager.createQuery("SELECT u FROM AppUser u", AppUser.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(AppUser.class, id) != null;
//     }

//     @Override
//     public <S extends AppUser> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(u) FROM AppUser u WHERE u = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(u) FROM AppUser u", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends AppUser> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT u FROM AppUser u WHERE u = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(AppUser.class, id));
//         }
//     }

//     @Override
//     public List<AppUser> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT u FROM AppUser u WHERE u.id IN :ids", AppUser.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<AppUser> saveAllAndFlush(@NonNull Iterable<AppUser> entities) {
//         for (AppUser user : entities) {
//             entityManager.persist(user);
//         }
//         entityManager.flush();
//         return (List<AppUser>) entities;
//     }

//     @Override
//     public AppUser getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(AppUser.class, id);
//     }

//     @Override
//     public Optional<AppUser> findById(@NonNull Long id) {
//         AppUser user = entityManager.find(AppUser.class, id);
//         return Optional.ofNullable(user);
//     }

//     @Override
//     public Optional<AppUser> findByUsername(@NonNull String username) {
//         return entityManager.createQuery("SELECT u FROM AppUser u WHERE u.username = :username", AppUser.class)
//                 .setParameter("username", username)
//                 .getResultList()
//                 .stream()
//                 .findFirst();
//     }
// }