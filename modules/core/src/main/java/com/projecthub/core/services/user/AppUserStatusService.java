package com.projecthub.core.services.user;

import java.util.UUID;

public interface AppUserStatusService {
    void updateStatus(UUID userId, String status);
}
