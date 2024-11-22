package org.biamn.ds2024.device_microservice.repository;

import org.biamn.ds2024.device_microservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findById(UUID id);

    void deleteById(UUID userId);
}
