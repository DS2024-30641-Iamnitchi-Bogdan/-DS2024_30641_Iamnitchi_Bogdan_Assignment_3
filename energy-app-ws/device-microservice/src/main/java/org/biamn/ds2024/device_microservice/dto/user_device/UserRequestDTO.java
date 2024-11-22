package org.biamn.ds2024.device_microservice.dto.user_device;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    private UUID id;
    private String name;
    private String email;
}
