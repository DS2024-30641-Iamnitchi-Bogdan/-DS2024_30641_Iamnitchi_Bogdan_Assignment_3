package org.biamn.ds2024.user_microservice.dto.user_device;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUserResponseDTO {

    private UUID id;
    private String name;
    private String email;
}
