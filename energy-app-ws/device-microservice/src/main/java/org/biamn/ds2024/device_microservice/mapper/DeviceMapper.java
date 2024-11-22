package org.biamn.ds2024.device_microservice.mapper;

import org.biamn.ds2024.device_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.device_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.device_microservice.model.DeviceEntity;
import org.biamn.ds2024.device_microservice.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "user.id", target = "user_id")
    DeviceResponseDTO deviceEntityToDeviceResponseDTO(DeviceEntity deviceEntity);

    @Mapping(source = "user.id", target = "user_id")
    List<DeviceResponseDTO> deviceEntityListToDeviceResponseDTOList(List<DeviceEntity> deviceEntityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "deviceRequestDTO.name", target = "name")
    @Mapping(source = "user", target = "user")
    DeviceEntity deviceRequestDTOToDeviceEntity(DeviceRequestDTO deviceRequestDTO, UserEntity user);
}
