package org.biamn.ds2024.device_microservice.dto.device;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {
    private UUID id;
    private String name;
    private String location;
    private String description;
    private Double max_consumption;
    private Double avg_consumption;
    private UUID user_id;
}
