package com.projecthub.base.auth.service.oauth;

import com.projecthub.base.auth.api.dto.GithubUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GithubOAuthService {

    private static final Logger logger = LoggerFactory.getLogger(GithubOAuthService.class);
    private final RestTemplate restTemplate;
    @Value("${github.client.id}")
    private String githubClientId;
    @Value("${github.client.secret}")
    private String githubClientSecret;

    public GithubOAuthService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getGithubAccessToken(final String code) {
        final String tokenUrl = "https://github.com/login/oauth/access_token";

        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", this.githubClientId);
        requestBody.put("client_secret", this.githubClientSecret);
        requestBody.put("code", code);

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final ResponseEntity<Map<String, Object>> response = this.restTemplate.exchange(
            tokenUrl,
            HttpMethod.POST,
            new HttpEntity<>(requestBody, headers),
            new ParameterizedTypeReference<Map<String, Object>>() {
            }
        );

        final Map<String, Object> responseBody = response.getBody();
        if (null != responseBody && responseBody.containsKey("access_token")) {
            return responseBody.get("access_token").toString();
        }

        GithubOAuthService.logger.error("Failed to get GitHub access token");
        return null;
    }

    public GithubUserInfo getGithubUserInfo(final String accessToken) {
        final String userUrl = "https://api.github.com/user";

        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final ResponseEntity<GithubUserInfo> response = this.restTemplate.exchange(
            userUrl,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            GithubUserInfo.class
        );

        final GithubUserInfo userInfo = response.getBody();
        if (null != userInfo) {
            return userInfo;
        }

        GithubOAuthService.logger.error("Failed to get GitHub user info");
        return null;
    }
}
