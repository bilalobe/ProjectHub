package com.projecthub.base.auth.domain.repository;

import com.projecthub.base.auth.domain.entity.PasskeyCredential;
import com.yubico.webauthn.data.ByteArray;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasskeyCredentialRepository extends JpaRepository<PasskeyCredential, String> {
    List<PasskeyCredential> findAllByUserUsername(String username);

    Optional<PasskeyCredential> findByCredentialId(ByteArray credentialId);
}
