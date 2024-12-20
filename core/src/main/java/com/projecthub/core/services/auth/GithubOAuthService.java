package com.projecthub.core.services.auth;

import com.projecthub.core.models.GithubUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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

    public GithubOAuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getGithubAccessToken(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", githubClientId);
        requestBody.put("client_secret", githubClientSecret);
        requestBody.put("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                tokenUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        );

        Map<String, Object> responseBody = response.getBody();
        if (responseBody != null && responseBody.containsKey("access_token")) {
            return responseBody.get("access_token").toString();
        }

        logger.error("Failed to get GitHub access token");
        return null;
    }

    public GithubUserInfo getGithubUserInfo(String accessToken) {
        String userUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<GithubUserInfo> response = restTemplate.exchange(
                userUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GithubUserInfo.class
        );

        GithubUserInfo userInfo = response.getBody();
        if (userInfo != null) {
            return userInfo;
        }

        logger.error("Failed to get GitHub user info");
        return null;
    }
}