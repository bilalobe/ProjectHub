package com.projecthub.base.shared.security.benchmark;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.projecthub.base.BaseApplication;
import com.projecthub.base.shared.security.permission.Permission;
import com.projecthub.base.shared.security.service.BatchPermissionChecker;
import com.projecthub.base.shared.security.service.OptimizedSecurityService;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(2)
public class PermissionCheckBenchmark {
    
    private ConfigurableApplicationContext context;
    private OptimizedSecurityService securityService;
    private BatchPermissionChecker batchChecker;
    private AccessMgr accessManager;
    private Session session;
    private Permission[] permissions;
    
    @Setup
    public void setup() {
        context = SpringApplication.run(BaseApplication.class);
        securityService = context.getBean(OptimizedSecurityService.class);
        batchChecker = context.getBean(BatchPermissionChecker.class);
        accessManager = context.getBean(AccessMgr.class);
        
        // Create test session
        session = new Session();
        session.setUserId("test-user");
        session.setSessionId(UUID.randomUUID().toString());
        
        // Create test permissions
        permissions = new Permission[100];
        for (int i = 0; i < permissions.length; i++) {
            final int index = i;
            permissions[i] = new Permission() {
                @Override public String getResourceType() { return "test-resource"; }
                @Override public String getOperation() { return "op-" + index; }
                @Override public boolean requiresOwnership() { return false; }
                @Override public boolean isAdministrative() { return false; }
                @Override public String getResourceId() { return "id-" + index; }
            };
        }
        
        // Preload some permissions
        batchChecker.preloadPermissions("test-resource");
    }
    
    @TearDown
    public void tearDown() {
        if (context != null) {
            context.close();
        }
    }
    
    @Benchmark
    public void baselineFortressCheck(Blackhole blackhole) throws Exception {
        org.apache.directory.fortress.core.model.Permission fortressPermission = 
            new org.apache.directory.fortress.core.model.Permission("test-resource", "op-1");
        blackhole.consume(accessManager.checkAccess(session, fortressPermission));
    }
    
    @Benchmark
    public void singlePermissionCheckL1Cache(Blackhole blackhole) {
        blackhole.consume(securityService.hasPermission(session, permissions[0]));
    }
    
    @Benchmark
    public void singlePermissionCheckL2Cache(Blackhole blackhole) {
        // Clear L1 cache first to force L2 check
        securityService.invalidateSessionPermissions(session);
        blackhole.consume(securityService.hasPermission(session, permissions[0]));
    }
    
    @Benchmark
    public void batchPermissionCheck(Blackhole blackhole) {
        blackhole.consume(batchChecker.checkPermissions(session, Set.of(permissions)));
    }
    
    @Benchmark
    public void preloadedPermissionCheck(Blackhole blackhole) {
        // Use a preloaded permission type
        Permission preloadedPermission = new Permission() {
            @Override public String getResourceType() { return "test-resource"; }
            @Override public String getOperation() { return "read"; }
            @Override public boolean requiresOwnership() { return false; }
            @Override public boolean isAdministrative() { return false; }
            @Override public String getResourceId() { return null; }
        };
        blackhole.consume(securityService.hasPermission(session, preloadedPermission));
    }
    
    @Benchmark
    public void compositePermissionCheck(Blackhole blackhole) {
        Permission composite = new CompositePermission() {
            @Override public List<Permission> getSubPermissions() {
                return Arrays.asList(permissions[0], permissions[1], permissions[2]);
            }
            @Override public boolean allRequired() { return true; }
            @Override public String getResourceType() { return "test-resource"; }
            @Override public String getOperation() { return "composite"; }
            @Override public boolean requiresOwnership() { return false; }
            @Override public boolean isAdministrative() { return false; }
            @Override public String getResourceId() { return null; }
        };
        blackhole.consume(securityService.hasCompositePermission(session, composite));
    }
}