package org.biamn.ds2024.device_microservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.device_microservice.dto.*;
import org.biamn.ds2024.device_microservice.dto.device.DeviceRequestDTO;
import org.biamn.ds2024.device_microservice.dto.device.DeviceResponseDTO;
import org.biamn.ds2024.device_microservice.exceptions.exception.model.ExceptionCode;
import org.biamn.ds2024.device_microservice.exceptions.exception.model.ResourceNotFoundException;
import org.biamn.ds2024.device_microservice.mapper.DeviceMapper;
import org.biamn.ds2024.device_microservice.model.DeviceEntity;
import org.biamn.ds2024.device_microservice.model.UserEntity;
import org.biamn.ds2024.device_microservice.repository.DeviceRepository;
import org.biamn.ds2024.device_microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceService {
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    private final DeviceProducer deviceProducer;

    @Value("${spring.application.name:BACKEND}")
    private String applicationName;

    public List<DeviceResponseDTO> findAll() {
        log.info("Getting all devices for {}", applicationName);
        List<DeviceEntity> deviceEntityList = deviceRepository.findAll();
        return deviceMapper.deviceEntityListToDeviceResponseDTOList(deviceEntityList);
    }

    public DeviceResponseDTO save(DeviceRequestDTO deviceRequestDTO) {
        UserEntity userToBeAdded =  userRepository.findById(deviceRequestDTO.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(),
                        deviceRequestDTO.getUser_id()
                )));
        DeviceEntity deviceToBeAdded = deviceMapper.deviceRequestDTOToDeviceEntity(deviceRequestDTO, userToBeAdded);
        DeviceEntity deviceAdded  = deviceRepository.save(deviceToBeAdded);
        try {
            deviceProducer.produceMessage(deviceMapper.deviceEntityToDeviceResponseDTO(deviceAdded), "insert");
        } catch (Exception e) {
            log.error("Error while producing insert message: {}", e.getMessage());
        }
        return deviceMapper.deviceEntityToDeviceResponseDTO(deviceAdded);
    }

    public DeviceResponseDTO findByDeviceId(UUID deviceId) {
        return deviceRepository.findById(deviceId)
                .map(deviceMapper::deviceEntityToDeviceResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceId
                )));
    }

    public List<DeviceResponseDTO> findByUserId(UUID userId) {
        return deviceRepository.findByUserId(userId).stream()
                .map(deviceMapper::deviceEntityToDeviceResponseDTO)
                .collect(Collectors.toList());
    }

    public DeviceResponseDTO findByLocation(String deviceLocation) {
        return deviceRepository.findByLocation(deviceLocation)
                .map(deviceMapper::deviceEntityToDeviceResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceLocation
                )));
    }

    public DeviceResponseDTO findByName(String deviceName){
        return deviceRepository.findByName(deviceName)
                .map(deviceMapper::deviceEntityToDeviceResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceName
                )));
    }

    public CollectionResponseDTO<DeviceResponseDTO> findAllPaged(PageRequestDTO page) {
        Page<DeviceEntity> deviceEntityList = deviceRepository.findAll(PageRequest.of(
                page.getPageNumber(),
                page.getPageSize()
        ));
        List<DeviceResponseDTO> devices = deviceMapper.deviceEntityListToDeviceResponseDTOList(deviceEntityList.getContent());
        return new CollectionResponseDTO<>(devices, deviceEntityList.getTotalElements());
    }

    public DeviceResponseDTO updateById(UUID deviceId, DeviceRequestDTO deviceRequestDTO){
        DeviceEntity deviceEntity = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(),
                        deviceId
                )));

        deviceEntity.setName(deviceRequestDTO.getName());
        deviceEntity.setLocation(deviceRequestDTO.getLocation());
        deviceEntity.setDescription(deviceRequestDTO.getDescription());
        deviceEntity.setAvg_consumption(deviceRequestDTO.getAvg_consumption());
        deviceEntity.setMax_consumption(deviceRequestDTO.getMax_consumption());
        deviceEntity.setUser(userRepository.findById(deviceRequestDTO.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(),
                        deviceRequestDTO.getUser_id()
                ))));

        DeviceEntity updatedDeviceEntity = deviceRepository.save(deviceEntity);
        try {
            deviceProducer.produceMessage(deviceMapper.deviceEntityToDeviceResponseDTO(updatedDeviceEntity), "update");
        } catch (Exception e) {
            log.error("Error while producing update message: {}", e.getMessage());
        }
        return deviceMapper.deviceEntityToDeviceResponseDTO(updatedDeviceEntity);
    }

    public void deleteById(UUID deviceId){
        if (!deviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException(String.format(
                    ExceptionCode.ERR001_DEVICE_NOT_FOUND.getMessage(), deviceId));
        }
        try {
            deviceProducer.produceMessage(deviceId.toString(), "delete");
        } catch (Exception e) {
            log.error("Error while producing delete message: {}", e.getMessage());
        }
        deviceRepository.deleteById(deviceId);
    }

}
