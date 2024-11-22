package org.biamn.ds2024.user_microservice.repository;

import org.biamn.ds2024.user_microservice.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>{

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByFirstName(String name);
    Optional<UserEntity> findByLastName(String name);

    void deleteById(UUID userId);
}
