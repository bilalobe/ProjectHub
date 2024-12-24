package com.projecthub.core.services.user;

import com.projecthub.core.dto.AppUserDTO;
import com.projecthub.core.entities.AppUser;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.mappers.AppUserMapper;
import com.projecthub.core.repositories.jpa.AppUserJpaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AppUserQueryServiceImpl implements AppUserQueryService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserQueryServiceImpl.class);
    private static final String USER_NOT_FOUND = "User not found with ID: ";

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper userMapper;

    public AppUserQueryServiceImpl(AppUserJpaRepository appUserRepository, AppUserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<AppUserDTO> getAllUsers() {
        logger.info("Retrieving all users");
        return appUserRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public AppUserDTO getUserById(UUID id) {
        logger.info("Retrieving user with ID: {}", id);
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND + id));
        return userMapper.toDto(user);
    }
}
