// package com.projecthub.repository.jpa.impl;

// import com.projecthub.model.Team;
// import com.projecthub.repository.jpa.TeamJpaRepository;
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
// public class TeamJpaRepositoryImpl implements TeamJpaRepository {

//     @PersistenceContext
//     private EntityManager entityManager;

//     @Override
//     public boolean exists(@NonNull Example<Team> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(t) FROM Team t WHERE t = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count > 0;
//     }

//     @Override
//     public Team getById(@NonNull Long id) {
//         return entityManager.find(Team.class, id);
//     }

//     @Override
//     public List<Team> saveAll(@NonNull Iterable<Team> entities) {
//         for (Team team : entities) {
//             entityManager.persist(team);
//         }
//         return (List<Team>) entities;
//     }

//     @Override
//     public void delete(@NonNull Team entity) {
//         entityManager.remove(entity);
//     }

//     @Override
//     public Team saveAndFlush(@NonNull Team entity) {
//         entityManager.persist(entity);
//         entityManager.flush();
//         return entity;
//     }

//     @Override
//     public void deleteAll(@NonNull Iterable<? extends Team> entities) {
//         for (Team team : entities) {
//             entityManager.remove(team);
//         }
//     }

//     @Override
//     public void deleteAll() {
//         entityManager.createQuery("DELETE FROM Team").executeUpdate();
//     }

//     @Override
//     public void flush() {
//         entityManager.flush();
//     }

//     @Override
//     public Team getOne(@NonNull Long id) {
//         return entityManager.getReference(Team.class, id);
//     }

//     @Override
//     public <S extends Team, R> R findBy(@NonNull Example<S> example, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
//         S result = entityManager.createQuery("SELECT t FROM Team t WHERE t = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return queryFunction.apply(FluentQuery.FetchableFluentQuery.of(result));
//     }

//     @Override
//     public void deleteAllByIdInBatch(@NonNull Iterable<Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Team.class, id));
//         }
//     }

//     @Override
//     public void deleteAllInBatch(@NonNull Iterable<Team> entities) {
//         for (Team team : entities) {
//             entityManager.remove(team);
//         }
//     }

//     @Override
//     public void deleteAllInBatch() {
//         entityManager.createQuery("DELETE FROM Team").executeUpdate();
//     }

//     @Override
//     public <S extends Team> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
//         // Build dynamic query with sorting (simplified example)
//         String qlString = "SELECT t FROM Team t WHERE t = :example ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Team> List<S> findAll(@NonNull Example<S> example) {
//         return entityManager.createQuery("SELECT t FROM Team t WHERE t = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getResultList();
//     }

//     @Override
//     public <S extends Team> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {
//         List<S> results = entityManager.createQuery("SELECT t FROM Team t WHERE t = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count(example);
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public List<Team> findAll(@NonNull Sort sort) {
//         String qlString = "SELECT t FROM Team t ORDER BY " + sort.toString().replace(":", "").replace(" ", " ");
//         return entityManager.createQuery(qlString, Team.class)
//                 .getResultList();
//     }

//     @Override
//     public Page<Team> findAll(@NonNull Pageable pageable) {
//         List<Team> results = entityManager.createQuery("SELECT t FROM Team t", Team.class)
//                 .setFirstResult((int) pageable.getOffset())
//                 .setMaxResults(pageable.getPageSize())
//                 .getResultList();
//         long total = count();
//         return new PageImpl<>(results, pageable, total);
//     }

//     @Override
//     public boolean existsById(@NonNull Long id) {
//         return entityManager.find(Team.class, id) != null;
//     }

//     @Override
//     public <S extends Team> long count(@NonNull Example<S> example) {
//         Long count = entityManager.createQuery("SELECT COUNT(t) FROM Team t WHERE t = :example", Long.class)
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public long count() {
//         Long count = entityManager.createQuery("SELECT COUNT(t) FROM Team t", Long.class)
//                 .getSingleResult();
//         return count;
//     }

//     @Override
//     public <S extends Team> Optional<S> findOne(@NonNull Example<S> example) {
//         S result = entityManager.createQuery("SELECT t FROM Team t WHERE t = :example", example.getProbeType())
//                 .setParameter("example", example.getProbe())
//                 .getSingleResult();
//         return Optional.ofNullable(result);
//     }

//     @Override
//     public void deleteAllById(@NonNull Iterable<? extends Long> ids) {
//         for (Long id : ids) {
//             entityManager.remove(entityManager.getReference(Team.class, id));
//         }
//     }

//     @Override
//     public List<Team> findAllById(@NonNull Iterable<Long> ids) {
//         return entityManager.createQuery("SELECT t FROM Team t WHERE t.id IN :ids", Team.class)
//                 .setParameter("ids", ids)
//                 .getResultList();
//     }

//     @Override
//     public List<Team> saveAllAndFlush(@NonNull Iterable<Team> entities) {
//         for (Team team : entities) {
//             entityManager.persist(team);
//         }
//         entityManager.flush();
//         return (List<Team>) entities;
//     }

//     @Override
//     public Team getReferenceById(@NonNull Long id) {
//         return entityManager.getReference(Team.class, id);
//     }

//     @Override
//     public Optional<Team> findTeamById(@NonNull Long teamId) {
//         Team team = entityManager.find(Team.class, teamId);
//         return Optional.ofNullable(team);
//     }

//     @Override
//     public List<Team> findAll() {
//         return entityManager.createQuery("SELECT t FROM Team t", Team.class)
//                 .getResultList();
//     }

//     @Override
//     public Team save(@NonNull Team team) {
//         if (team.getId() == null) {
//             entityManager.persist(team);
//         } else {
//             entityManager.merge(team);
//         }
//         return team;
//     }

//     @Override
//     public void deleteById(@NonNull Long teamId) {
//         Team team = entityManager.find(Team.class, teamId);
//         if (team != null) {
//             entityManager.remove(team);
//         }
//     }

//     @Override
//     public Optional<Team> findById(@NonNull Long id) {
//         Team team = entityManager.find(Team.class, id);
//         return Optional.ofNullable(team);
//     }

//     @Override
//     public List<Team> findByCohortId(@NonNull Long cohortId) {
//         return entityManager.createQuery("SELECT t FROM Team t WHERE t.cohort.id = :cohortId", Team.class)
//                 .setParameter("cohortId", cohortId)
//                 .getResultList();
//     }
// }