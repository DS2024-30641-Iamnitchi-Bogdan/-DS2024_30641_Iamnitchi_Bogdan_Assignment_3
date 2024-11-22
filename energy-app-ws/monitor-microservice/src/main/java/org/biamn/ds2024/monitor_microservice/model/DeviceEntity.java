package org.biamn.ds2024.monitor_microservice.model;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "device")
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEntity implements Serializable {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "max_consumption", nullable = false)
    private Double max_consumption;

    @Column(name = "user_id", columnDefinition = "BINARY(16)")
    private UUID user_id;
}


