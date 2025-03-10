package com.projecthub.base.auth.service.token;

import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.utils.JwtUtil;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TokenFactory {
    private final JwtUtil jwtUtil;
    private final TokenProperties properties;

    public TokenFactory(final JwtUtil jwtUtil, final TokenProperties properties) {
        this.jwtUtil = jwtUtil;
        this.properties = properties;
    }

    public String createAccessToken(final AppUser user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList()));
        claims.put("userId", user.getId().toString());

        return this.jwtUtil.generateToken(user.getUsername(), claims);
    }

    public String createRefreshToken(final AppUser user) {
        return this.jwtUtil.generateRefreshToken(user.getUsername());
    }
}
