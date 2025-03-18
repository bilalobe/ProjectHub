package com.projecthub.gateway.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

/**
 * Converter that extracts Fortress RBAC roles and permissions from a JWT token.
 * 
 * <p>This class is responsible for converting JWT claims into Spring Security
 * {@link GrantedAuthority} objects that can be used for authorization decisions.</p>
 */
public class FortressGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLES_CLAIM = "roles";
    private static final String PERMISSIONS_CLAIM = "permissions";
    private static final String FORTRESS_NAMESPACE = "fortress";
    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * Converts JWT claims into a collection of granted authorities.
     * 
     * <p>This method extracts roles and permissions from the JWT claims
     * and converts them into Spring Security authorities.</p>
     * 
     * @param jwt The JWT token to extract authorities from
     * @return A collection of granted authorities
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        
        // Extract roles and permissions from claims
        Collection<String> roles = extractRoles(claims);
        Collection<String> permissions = extractPermissions(claims);
        
        // Combine roles and permissions into a single collection of authorities
        return Stream.concat(
                roles.stream().map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role)),
                permissions.stream().map(SimpleGrantedAuthority::new)
            ).collect(Collectors.toList());
    }

    /**
     * Extracts roles from JWT claims.
     * 
     * @param claims The JWT claims
     * @return A collection of role names
     */
    @SuppressWarnings("unchecked")
    private Collection<String> extractRoles(Map<String, Object> claims) {
        Object rolesObj = claims.get(ROLES_CLAIM);
        if (rolesObj instanceof Collection) {
            return ((Collection<Object>) rolesObj).stream()
                    .map(Object::toString)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList());
        }
        
        // Try to find roles in Fortress namespace
        Map<String, Object> fortressData = getFortressNamespace(claims);
        if (fortressData != null && fortressData.containsKey(ROLES_CLAIM)) {
            Object fortressRoles = fortressData.get(ROLES_CLAIM);
            if (fortressRoles instanceof Collection) {
                return ((Collection<Object>) fortressRoles).stream()
                        .map(Object::toString)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList());
            }
        }
        
        return Collections.emptyList();
    }

    /**
     * Extracts permissions from JWT claims.
     * 
     * @param claims The JWT claims
     * @return A collection of permission strings
     */
    @SuppressWarnings("unchecked")
    private Collection<String> extractPermissions(Map<String, Object> claims) {
        List<String> permissions = new ArrayList<>();
        
        Object permissionsObj = claims.get(PERMISSIONS_CLAIM);
        if (permissionsObj instanceof Collection) {
            permissions.addAll(((Collection<Object>) permissionsObj).stream()
                    .map(Object::toString)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toList()));
        }
        
        // Try to find permissions in Fortress namespace
        Map<String, Object> fortressData = getFortressNamespace(claims);
        if (fortressData != null && fortressData.containsKey(PERMISSIONS_CLAIM)) {
            Object fortressPermissions = fortressData.get(PERMISSIONS_CLAIM);
            if (fortressPermissions instanceof Collection) {
                permissions.addAll(((Collection<Object>) fortressPermissions).stream()
                        .map(Object::toString)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList()));
            }
        }
        
        return permissions;
    }

    /**
     * Gets the Fortress namespace from JWT claims.
     * 
     * @param claims The JWT claims
     * @return The Fortress namespace map or null if not present
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getFortressNamespace(Map<String, Object> claims) {
        Object fortressObj = claims.get(FORTRESS_NAMESPACE);
        if (fortressObj instanceof Map) {
            return (Map<String, Object>) fortressObj;
        }
        return null;
    }
}