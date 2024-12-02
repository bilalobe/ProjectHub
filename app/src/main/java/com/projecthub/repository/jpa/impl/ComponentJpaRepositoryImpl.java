// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Component;
// import com.projecthub.repository.jpa.ComponentJpaRepository;
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
// public class ComponentJpaRepositoryImpl implements ComponentJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Component> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Component c WHERE c = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Component getById(@NonNull Long id) {
//         return entityManager.find(Component.class, id);
//     }

//     @Override
//     public List<Component> saveAll(@NonNull Iterable<Component> entities) {
//         for (Component component : entities) {
//             entityManager.persist(component);
//         }
//         return (List<Component>) entities;
//     }

//     @Override
//     public void delete(@NonNull Component entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Component saveAndFlush(@NonNull Component entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Component> entities) {
//         for (Component component : entities) {
//             entityManager.remove(component);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Component").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Component getOne(@NonNull Long id) {
//         return entityManager.getReference(Component.class, id);
//     }

//     @Override
//     public <S extends Component, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT c FROM Component c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Component.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Component> entities) {
//         for (Component component : entities) {
//             entityManager.remove(component);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Component").executeUpdate();
//     }

//     @Override
//     public <S extends Component> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT c FROM Component c WHERE c = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Component> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT c FROM Component c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Component> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT c FROM Component c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Component> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT c FROM Component c ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Component.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Component> findAll(@NonNull Pageable pageable) {
//         List<Component> results = entityManager.createQuery("SELECT c FROM Component c", Component.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Component.class, id) != null;
//     }

//     @Override
//     public <S extends Component> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Component c WHERE c = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(c) FROM Component c", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Component> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT c FROM Component c WHERE c = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Component.class, id));
//         }
//     }

//     @Override
//     public List<Component> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT c FROM Component c WHERE c.id IN :ids", Component.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Component> saveAllAndFlush(@NonNull Iterable<Component> entities) {
//         for (Component component : entities) {
//             entityManager.persist(component);
//         }
//         entityManager.flush();
//         return (List<Component>) entities;
//     }

//     @Override
//     public Component getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Component.class, id);
//     }

//     @Override
//     public Optional<Component> findById(@NonNull Long id) {
//         Component component = entityManager.find(Component.class, id);
//         return Optional.ofNullable(component);
//     }

//     @Override
//     public List<Component> findByProjectId(@NonNull Long projectId) {
//         return entityManager.createQuery("SELECT c FROM Component c WHERE c.project.id = :projectId", Component.class)
//                 .setParameter("projectId", projectId)
//                 .getResultList();
//     }
// }