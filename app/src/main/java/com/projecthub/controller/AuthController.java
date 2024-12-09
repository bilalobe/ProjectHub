package com.projecthub.controller;

import com.projecthub.dto.AppUserDTO;
import com.projecthub.service.AppUserService;
import com.projecthub.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user authentication. Provides endpoints for user
 * registration and login.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * Constructs an AuthController with the required dependencies.
     *
     * @param appUserService        the service for managing application users
     * @param authenticationManager the authentication manager for handling
     *                              authentication
     * @param jwtUtil               the utility for generating JWT tokens
     */
    public AuthController(AppUserService appUserService, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.appUserService = appUserService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Registers a new user with the provided details.
     *
     * @param userDTO the data transfer object containing user details
     * @return the created AppUserDTO
     */
    @PostMapping("/register")
    public AppUserDTO registerUser(@RequestBody AppUserDTO userDTO) {
        return appUserService.createUser(userDTO, userDTO.getPassword());
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     *
     * @param userDTO the data transfer object containing user credentials
     * @return the generated JWT token
     */
    @PostMapping("/login")
    public String login(@RequestBody AppUserDTO userDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtUtil.generateToken(userDTO.getUsername());
        return jwtToken;
    }
}