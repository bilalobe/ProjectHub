package com.projecthub.base.user.application.profile.service;

import java.util.UUID;

public interface AppUserStatusService {
    void updateStatus(UUID userId, String status);
}
