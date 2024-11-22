package org.biamn.ds2024.monitor_microservice.mapper;

import org.biamn.ds2024.monitor_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeviceUserMapper {

    DeviceResponseDTO userEntityToUserResponseDTO(DeviceEntity deviceEntity);

    List<DeviceResponseDTO> userDeviceEntityListToUserResponseDTOList(List<DeviceEntity> deviceEntityList);

    DeviceEntity userRequestDTOToUserEntity(DeviceRequestDTO userRequestDTO);
}
