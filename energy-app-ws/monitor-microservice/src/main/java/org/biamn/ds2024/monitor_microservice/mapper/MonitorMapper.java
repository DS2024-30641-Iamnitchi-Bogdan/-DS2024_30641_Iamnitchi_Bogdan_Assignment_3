package org.biamn.ds2024.monitor_microservice.mapper;

import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.monitor.MonitorResponseDTO;
import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.biamn.ds2024.monitor_microservice.model.MonitorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = DeviceUserMapper.class)
public interface MonitorMapper {

    @Mapping(source = "device.id", target = "deviceId")
    MonitorResponseDTO monitorEntityToMonitorResponseDTO(MonitorEntity monitorEntity);

    @Mapping(source = "device.id", target = "deviceId")
    List<MonitorResponseDTO> monitorEntityListToMonitorResponseDTOList(List<MonitorEntity> monitorEntityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "device", target = "device")
    @Mapping(source = "monitorRequestDTO.max_consumption", target = "max_consumption")
    MonitorEntity monitorRequestDTOToMonitorEntity(MonitorRequestDTO monitorRequestDTO, DeviceEntity device);
}
