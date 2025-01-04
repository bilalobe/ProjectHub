package com.projecthub.base.user.application.query.service;

import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import com.projecthub.base.user.domain.entity.AppUser;
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
    public List<AppUserDTO> getAlluser() {
        logger.info("Retrieving all user");
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

    @Override
    public List<AppUserDTO> getAllUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }
}
