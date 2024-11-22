package org.biamn.ds2024.monitor_microservice.service.device;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.monitor_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.monitor_microservice.exceptions.exception.model.ExceptionCode;
import org.biamn.ds2024.monitor_microservice.exceptions.exception.model.ResourceNotFoundException;
import org.biamn.ds2024.monitor_microservice.mapper.DeviceUserMapper;
import org.biamn.ds2024.monitor_microservice.model.DeviceEntity;
import org.biamn.ds2024.monitor_microservice.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceUserMapper deviceUserMapper;
    public final DeviceRepository deviceRepository;

    public DeviceResponseDTO findById(UUID deviceId) {
        return deviceRepository.findById(deviceId)
                .map(deviceUserMapper::userEntityToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(),
                        deviceId
                )));
    }

    public List<DeviceResponseDTO> findAll() {
        List<DeviceEntity> userEntityList = deviceRepository.findAll();
        return deviceUserMapper.userDeviceEntityListToUserResponseDTOList(userEntityList);
    }

    public DeviceResponseDTO save(DeviceRequestDTO deviceRequestDTO) {
        deviceRepository.findById(deviceRequestDTO.getId())
                .ifPresent(device -> {
                    throw new ResourceNotFoundException(String.format(
                            ExceptionCode.ERR002_DEVICE_DUPLICATED.getMessage(),
                            deviceRequestDTO.getId()
                    ));
                });
        DeviceEntity deviceToBeAdded = deviceUserMapper.userRequestDTOToUserEntity(deviceRequestDTO);
        DeviceEntity deviceAdded = deviceRepository.save(deviceToBeAdded);
        return deviceUserMapper.userEntityToUserResponseDTO(deviceAdded);
    }

    public DeviceResponseDTO updateById(UUID deviceId, DeviceRequestDTO deviceRequestDTO){
        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceId
                )));
        deviceEntity.setId(deviceId);
        deviceEntity.setName(deviceRequestDTO.getName());
        deviceEntity.setLocation(deviceRequestDTO.getLocation());
        deviceEntity.setMax_consumption(deviceRequestDTO.getMax_consumption());
        deviceEntity.setUser_id(deviceRequestDTO.getUser_id());

        DeviceEntity updatedDeviceEntity = deviceRepository.save(deviceEntity);
        return deviceUserMapper.userEntityToUserResponseDTO(updatedDeviceEntity);
    }

    public void deleteById(UUID deviceId){
        if (!deviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException(String.format(
                    ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(), deviceId));
        }
        deviceRepository.deleteById(deviceId);
    }

}
