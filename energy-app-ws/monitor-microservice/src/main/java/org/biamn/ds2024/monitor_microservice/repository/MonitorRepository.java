package org.biamn.ds2024.monitor_microservice.repository;

import org.biamn.ds2024.monitor_microservice.model.MonitorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MonitorRepository extends JpaRepository<MonitorEntity, UUID>{

    Optional<MonitorEntity> findById(UUID id);

    List<MonitorEntity> findMonitorEntitiesByDeviceId(UUID deviceId);

    @Query("SELECT m FROM MonitorEntity m WHERE m.device.id = :deviceId AND (m.timestamp >= :startOfDay AND m.timestamp < :endOfDay) ORDER BY m.timestamp ASC")
    List<MonitorEntity> findAllByDay(
            @Param("deviceId") UUID deviceId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);


    void deleteById(UUID id);
}
