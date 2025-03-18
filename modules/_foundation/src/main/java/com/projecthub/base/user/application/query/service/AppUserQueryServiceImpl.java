package com.projecthub.base.user.application.query.service;

import com.projecthub.base.repository.jpa.AppUserJpaRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.user.api.dto.AppUserDTO;
import com.projecthub.base.user.api.mapper.AppUserMapper;
import com.projecthub.base.user.domain.entity.AppUser;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AppUserQueryServiceImpl implements AppUserQueryService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserQueryServiceImpl.class);
    @NonNls
    private static final String USER_NOT_FOUND = "User not found with ID: ";

    private final AppUserJpaRepository appUserRepository;
    private final AppUserMapper userMapper;

    public AppUserQueryServiceImpl(final AppUserJpaRepository appUserRepository, final AppUserMapper userMapper) {
        this.appUserRepository = appUserRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<AppUserDTO> getAlluser() {
        AppUserQueryServiceImpl.logger.info("Retrieving all user");
        return this.appUserRepository.findAll().stream()
            .map(this.userMapper::toDto)
            .toList();
    }

    @Override
    public AppUserDTO getUserById(final UUID id) {
        AppUserQueryServiceImpl.logger.info("Retrieving user with ID: {}", id);
        final AppUser user = this.appUserRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserQueryServiceImpl.USER_NOT_FOUND + id));
        return this.userMapper.toDto(user);
    }

    @Override
    public List<AppUserDTO> getAllUsers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllUsers'");
    }
}
