package org.biamn.ds2024.monitor_microservice.dto.monitor;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorRequestDTO {

    private LocalDateTime timestamp;
    private Double avg_consumption;
    private Double max_consumption;
    private UUID deviceId;
}
