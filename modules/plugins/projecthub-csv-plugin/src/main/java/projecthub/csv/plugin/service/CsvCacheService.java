package projecthub.csv.plugin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CsvCacheService {
    private static final Logger logger = LoggerFactory.getLogger(CsvCacheService.class);

    private final Map<String, CacheEntry<?>> cache = new ConcurrentHashMap<>();
    private final Map<String, WatchService> fileWatchers = new ConcurrentHashMap<>();

    public CsvCacheService() {
    }

    private static class CacheEntry<T> {
        final List<T> data;
        final Instant lastModified;

        CacheEntry(List<T> data, Instant lastModified) {
            this.data = data;
            this.lastModified = lastModified;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getCachedData(String filePath, Class<T> type) {
        CacheEntry<?> entry = cache.get(filePath);
        if (entry != null && isValid(filePath, entry)) {
            return (List<T>) entry.data;
        }
        return null;
    }

    public <T> void updateCache(String filePath, List<T> data) {
        try {
            Path path = Paths.get(filePath);
            Instant lastModified = Files.getLastModifiedTime(path).toInstant();
            cache.put(filePath, new CacheEntry<>(data, lastModified));
            setupFileWatcher(filePath);
            logger.debug("Updated cache for file: {}", filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.warn("Failed to update cache for file: {}", filePath, e);
        }
    }

    public void invalidateCache(String filePath) {
        cache.remove(filePath);
        logger.debug("Invalidated cache for file: {}", filePath);
    }

    private static boolean isValid(String filePath, CacheEntry<?> entry) {
        try {
            Path path = Paths.get(filePath);
            Instant fileLastModified = Files.getLastModifiedTime(path).toInstant();
            return !fileLastModified.isAfter(entry.lastModified);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.warn("Failed to check cache validity for file: {}", filePath, e);
            return false;
        }
    }

    private void setupFileWatcher(String filePath) {
        if (fileWatchers.containsKey(filePath)) {
            return;
        }

        try {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(filePath).getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            Thread watchThread = new Thread(() -> {
                try {
                    while (true) {
                        WatchKey key = watcher.take();
                        for (WatchEvent<?> event : key.pollEvents()) {
                            Path changed = (Path) event.context();
                            if (Paths.get(filePath).getFileName().equals(changed)) {
                                invalidateCache(filePath);
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            watchThread.setDaemon(true);
            watchThread.start();

            fileWatchers.put(filePath, watcher);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.warn("Failed to setup file watcher for: {}", filePath, e);
        }
    }
}
