package com.projecthub.base.shared.security.benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.directory.fortress.core.model.Session;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.projecthub.base.BaseApplication;
import com.projecthub.base.shared.security.cache.PermissionCacheService;
import com.projecthub.base.shared.security.permission.Permission;
import com.projecthub.base.shared.security.service.BatchPermissionChecker;
import com.projecthub.base.shared.security.service.OptimizedSecurityService;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
public class MemoryUsageBenchmark {
    
    private ConfigurableApplicationContext context;
    private OptimizedSecurityService securityService;
    private BatchPermissionChecker batchChecker;
    private PermissionCacheService cacheService;
    private Session session;
    private MemoryMXBean memoryBean;
    private List<Permission> permissions;
    
    @Setup
    public void setup() {
        context = SpringApplication.run(BaseApplication.class);
        securityService = context.getBean(OptimizedSecurityService.class);
        batchChecker = context.getBean(BatchPermissionChecker.class);
        cacheService = context.getBean(PermissionCacheService.class);
        memoryBean = ManagementFactory.getMemoryMXBean();
        
        session = new Session();
        session.setUserId("test-user");
        session.setSessionId(UUID.randomUUID().toString());
        
        // Create large number of test permissions
        permissions = new ArrayList<>();
        for (int i = 0; i < 100_000; i++) {
            final int index = i;
            permissions.add(new Permission() {
                @Override public String getResourceType() { return "test-resource"; }
                @Override public String getOperation() { return "op-" + index; }
                @Override public boolean requiresOwnership() { return false; }
                @Override public boolean isAdministrative() { return false; }
                @Override public String getResourceId() { return "id-" + index; }
            });
        }
    }
    
    @TearDown
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }
    
    private long getUsedMemory() {
        System.gc(); // Request GC to get more accurate memory usage
        return memoryBean.getHeapMemoryUsage().getUsed();
    }
    
    @Benchmark
    public void measureL1CacheMemoryUsage(Blackhole blackhole) {
        long beforeMemory = getUsedMemory();
        
        // Cache large number of permissions in L1
        permissions.forEach(p -> 
            securityService.hasPermission(session, p));
            
        long afterMemory = getUsedMemory();
        blackhole.consume(afterMemory - beforeMemory);
    }
    
    @Benchmark
    public void measurePreloadMemoryUsage(Blackhole blackhole) {
        long beforeMemory = getUsedMemory();
        
        // Preload multiple resource types
        List<String> resourceTypes = List.of(
            "school", "cohort", "project", "team", 
            "task", "milestone", "submission"
        );
        
        resourceTypes.forEach(type -> 
            batchChecker.preloadPermissions(type));
            
        long afterMemory = getUsedMemory();
        blackhole.consume(afterMemory - beforeMemory);
    }
    
    @Benchmark
    public void measureMemoryUnderLoad(Blackhole blackhole) {
        long beforeMemory = getUsedMemory();
        
        // Simulate high load with multiple operations
        for (int i = 0; i < 10; i++) {
            Session userSession = new Session();
            userSession.setUserId("user-" + i);
            userSession.setSessionId(UUID.randomUUID().toString());
            
            // Mix of different operations
            permissions.subList(0, 1000).forEach(p -> 
                securityService.hasPermission(userSession, p));
                
            batchChecker.checkPermissions(userSession, 
                Set.copyOf(permissions.subList(1000, 2000)));
                
            if (i % 2 == 0) {
                batchChecker.preloadPermissions("resource-type-" + i);
            }
        }
        
        long afterMemory = getUsedMemory();
        blackhole.consume(afterMemory - beforeMemory);
    }
    
    @Benchmark
    public void measureGarbageCollectionEffectiveness(Blackhole blackhole) {
        long beforeMemory = getUsedMemory();
        
        // Create and cache many permissions
        for (int i = 0; i < 5; i++) {
            Session userSession = new Session();
            userSession.setUserId("user-" + i);
            userSession.setSessionId(UUID.randomUUID().toString());
            
            permissions.forEach(p -> 
                securityService.hasPermission(userSession, p));
                
            // Invalidate to trigger GC
            securityService.invalidateSessionPermissions(userSession);
        }
        
        // Force aggressive GC through memory pressure
        List<byte[]> memoryPressure = new ArrayList<>();
        try {
            while (true) {
                memoryPressure.add(new byte[1024 * 1024]); // Allocate 1MB chunks
            }
        } catch (OutOfMemoryError e) {
            // Expected - we wanted to trigger GC
            memoryPressure.clear();
        }
        
        long afterMemory = getUsedMemory();
        blackhole.consume(afterMemory - beforeMemory);
    }
    
    @Benchmark
    public void measureCacheEvictionEfficiency(Blackhole blackhole) {
        // Pre-warm cache
        permissions.forEach(p -> 
            securityService.hasPermission(session, p));
            
        long beforeMemory = getUsedMemory();
        
        // Force eviction through memory pressure
        for (int i = 0; i < 10; i++) {
            List<Permission> newPermissions = new ArrayList<>();
            for (int j = 0; j < 10_000; j++) {
                final int index = j;
                newPermissions.add(new Permission() {
                    @Override public String getResourceType() { 
                        return "resource-" + i; 
                    }
                    @Override public String getOperation() { 
                        return "op-" + index; 
                    }
                    @Override public boolean requiresOwnership() { 
                        return false; 
                    }
                    @Override public boolean isAdministrative() { 
                        return false; 
                    }
                    @Override public String getResourceId() { 
                        return "id-" + index; 
                    }
                });
            }
            
            newPermissions.forEach(p -> 
                securityService.hasPermission(session, p));
        }
        
        long afterMemory = getUsedMemory();
        blackhole.consume(afterMemory - beforeMemory);
    }
}