package org.biamn.ds2024.monitor_microservice.dto.monitor;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MonitorResponseDTO {

    private UUID id;
    private LocalDateTime timestamp;
    private Double avg_consumption;
    private Double max_consumption;
    private UUID deviceId;
}
