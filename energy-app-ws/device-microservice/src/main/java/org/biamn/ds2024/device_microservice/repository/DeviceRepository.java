package org.biamn.ds2024.device_microservice.repository;

import org.biamn.ds2024.device_microservice.model.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID>{

    Optional<DeviceEntity> findById(UUID id);

    Optional<DeviceEntity> findByName(String name);

    Optional<DeviceEntity> findByLocation(String email);

    List<DeviceEntity> findByUserId(UUID userId);

    void deleteById(UUID id);
}
