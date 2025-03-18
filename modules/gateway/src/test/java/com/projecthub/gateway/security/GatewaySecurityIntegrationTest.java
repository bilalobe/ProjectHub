package com.projecthub.gateway.security;

import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Session;
import org.apache.directory.fortress.core.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Integration tests for the API Gateway's security enforcement.
 * 
 * <p>This test verifies that the gateway correctly enforces Fortress RBAC
 * policies for different types of users and API endpoints.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class GatewaySecurityIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @MockBean
    private AccessMgr accessManager;

    private String adminToken;
    private String instructorToken;
    private String studentToken;

    @BeforeEach
    public void setup() throws Exception {
        // Setup mock JWT decoder for testing
        adminToken = createToken("admin", "ADMIN");
        instructorToken = createToken("instructor", "INSTRUCTOR");
        studentToken = createToken("student", "STUDENT");

        // Configure Fortress mocks for different user types
        configureFortressMock();
    }

    @Test
    public void testPublicEndpointsAccessible() {
        // Public endpoints should be accessible without authentication
        webTestClient.get().uri("/api/auth/login")
                .exchange()
                .expectStatus().isOk();

        webTestClient.post().uri("/api/auth/register")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testAdminCanAccessAllEndpoints() {
        // Admin should be able to access all endpoints
        webTestClient.get().uri("/api/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri("/api/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri("/api/submissions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testInstructorCanAccessLimitedEndpoints() {
        // Instructor can access projects and submissions but not admin endpoints
        webTestClient.get().uri("/api/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + instructorToken)
                .exchange()
                .expectStatus().isOk();

        webTestClient.get().uri("/api/submissions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + instructorToken)
                .exchange()
                .expectStatus().isOk();

        // Should be denied access to sensitive admin endpoints
        webTestClient.get().uri("/api/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + instructorToken)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void testStudentCanAccessOwnResources() {
        // Student can access their own submissions but not others
        webTestClient.get().uri("/api/submissions/student/{studentId}", "student-123")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken)
                .exchange()
                .expectStatus().isOk();

        // Should be denied access to instructor endpoints
        webTestClient.get().uri("/api/submissions/grade")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void testUnauthenticatedAccessDenied() {
        // Unauthenticated access to protected resources should be denied
        webTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isUnauthorized();

        webTestClient.get().uri("/api/projects")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    /**
     * Creates a mock JWT token for testing.
     */
    private String createToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("userId", UUID.randomUUID().toString());
        claims.put("roles", Collections.singletonList(role));

        Jwt jwt = Jwt.withTokenValue(username + "-token")
                .header("alg", "none")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject(username)
                .build();

        when(jwtDecoder.decode(username + "-token")).thenReturn(Mono.just(jwt));

        return username + "-token";
    }

    /**
     * Configures the Fortress mock for different test scenarios.
     */
    private void configureFortressMock() throws Exception {
        // Setup for admin user - allow all permissions
        when(accessManager.checkAccess(Mockito.argThat(session -> 
                session.getUser().getName().equals("admin")), any(Permission.class)))
                .thenReturn(new Permission());

        // Setup for instructor - allow project and submission related permissions
        when(accessManager.checkAccess(Mockito.argThat(session -> 
                session.getUser().getName().equals("instructor") &&
                (session.getUser().getRole().contains("INSTRUCTOR") ||
                 "PROJECT".equals(((Permission)Mockito.any()).getObjName()) || 
                 "SUBMISSION".equals(((Permission)Mockito.any()).getObjName()))), 
                any(Permission.class)))
                .thenReturn(new Permission());

        // Setup for student - allow only their own submissions
        when(accessManager.checkAccess(Mockito.argThat(session -> 
                session.getUser().getName().equals("student") &&
                "SUBMISSION".equals(((Permission)Mockito.any()).getObjName()) && 
                "READ".equals(((Permission)Mockito.any()).getOpName())), 
                any(Permission.class)))
                .thenReturn(new Permission());

        // Deny all other permission checks
        when(accessManager.checkAccess(any(Session.class), any(Permission.class)))
                .thenThrow(new org.apache.directory.fortress.core.SecurityException());
    }
}