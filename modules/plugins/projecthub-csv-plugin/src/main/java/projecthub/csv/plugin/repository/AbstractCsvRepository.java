package projecthub.csv.plugin.repository;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import projecthub.csv.plugin.helper.CsvFileHelper;
import projecthub.csv.plugin.helper.CsvHelper;

import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base implementation for CSV repositories.
 * Provides caching, error handling, and common CRUD operations.
 *
 * @param <T> the entity type
 */
public abstract class AbstractCsvRepository<T> implements CsvRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCsvRepository.class);
    private final ConcurrentHashMap<UUID, T> cache = new ConcurrentHashMap<>();
    protected final Validator validator;
    private final String filepath;
    private final Class<T> entityClass;
    private final MappingStrategy<T> mappingStrategy;

    protected AbstractCsvRepository(String filepath, Class<T> entityClass,
                                  MappingStrategy<T> mappingStrategy, Validator validator) {
        this.filepath = filepath;
        this.entityClass = entityClass;
        this.mappingStrategy = mappingStrategy;
        this.validator = validator;
    }

    protected abstract UUID getEntityId(T entity);
    protected abstract void validateEntity(T entity);

    @Override
    @CacheEvict(value = "csvEntities", key = "#root.targetClass + '_' + #entity")
    public T save(T entity) {
        validateEntity(entity);
        try {
            CsvFileHelper.backupCSVFile(filepath);
            List<T> entities = findAll();
            UUID id = getEntityId(entity);
            entities.removeIf(e -> Objects.equals(getEntityId(e), id));
            entities.add(entity);
            cache.put(id, entity);
            CsvHelper.writeBeansToCsv(filepath, entityClass, entities, mappingStrategy);
            logger.info("Entity saved successfully: {}", entity);
            return entity;
        } catch (RuntimeException e) {
            logger.error("Error saving entity to CSV", e);
            throw new CsvRepositoryException("Error saving entity to CSV", e);
        }
    }

    @Override
    @CacheEvict(value = "csvEntities", allEntries = true)
    public List<T> saveAll(List<T> entities) {
        entities.forEach(this::validateEntity);
        try {
            CsvFileHelper.backupCSVFile(filepath);
            Map<UUID, T> entityMap = new HashMap<>();
            entities.forEach(e -> entityMap.put(getEntityId(e), e));

            List<T> existing = findAll();
            existing.removeIf(e -> entityMap.containsKey(getEntityId(e)));
            existing.addAll(entities);

            cache.putAll(entityMap);
            CsvHelper.writeBeansToCsv(filepath, entityClass, existing, mappingStrategy);
            logger.info("Batch save successful, {} entities saved", Integer.valueOf(entities.size()));
            return entities;
        } catch (RuntimeException e) {
            logger.error("Error batch saving entities to CSV", e);
            throw new CsvRepositoryException("Error batch saving entities to CSV", e);
        }
    }

    @Override
    @Cacheable(value = "csvEntities", key = "#root.targetClass + '_' + #id")
    public Optional<T> findById(UUID id) {
        T cached = cache.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        return findAll().stream()
                       .filter(e -> Objects.equals(getEntityId(e), id))
                       .findFirst();
    }

    @Override
    @Cacheable(value = "csvEntities", key = "#root.targetClass + '_all'")
    public List<T> findAll() {
        try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
            List<T> entities = new CsvToBeanBuilder<T>(reader)
                .withMappingStrategy(mappingStrategy)
                .build()
                .parse();
            entities.forEach(e -> cache.put(getEntityId(e), e));
            return entities;
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("Error reading entities from CSV", e);
            throw new CsvRepositoryException("Error reading entities from CSV", e);
        }
    }

    @Override
    @CacheEvict(value = "csvEntities", key = "#root.targetClass + '_' + #id")
    public void deleteById(UUID id) {
        try {
            CsvFileHelper.backupCSVFile(filepath);
            List<T> entities = findAll();
            entities.removeIf(e -> Objects.equals(getEntityId(e), id));
            cache.remove(id);
            CsvHelper.writeBeansToCsv(filepath, entityClass, entities, mappingStrategy);
            logger.info("Entity deleted successfully: {}", id);
        } catch (RuntimeException e) {
            logger.error("Error deleting entity from CSV", e);
            throw new CsvRepositoryException("Error deleting entity from CSV", e);
        }
    }

    @Override
    @CacheEvict(value = "csvEntities", allEntries = true)
    public void deleteAllById(List<UUID> ids) {
        try {
            CsvFileHelper.backupCSVFile(filepath);
            List<T> entities = findAll();
            Collection<UUID> idSet = new HashSet<>(ids);
            entities.removeIf(e -> idSet.contains(getEntityId(e)));
            ids.forEach(cache::remove);
            CsvHelper.writeBeansToCsv(filepath, entityClass, entities, mappingStrategy);
            logger.info("Batch delete successful, {} entities deleted", Integer.valueOf(ids.size()));
        } catch (RuntimeException e) {
            logger.error("Error batch deleting entities from CSV", e);
            throw new CsvRepositoryException("Error batch deleting entities from CSV", e);
        }
    }
}
