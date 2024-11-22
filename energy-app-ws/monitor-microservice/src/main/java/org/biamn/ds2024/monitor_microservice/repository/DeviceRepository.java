package org.biamn.ds2024.monitor_microservice.repository;

import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface DeviceRepository extends JpaRepository<DeviceEntity, UUID> {

    Optional<DeviceEntity> findById(UUID deviceId);

    void deleteById(UUID deviceId);
}
