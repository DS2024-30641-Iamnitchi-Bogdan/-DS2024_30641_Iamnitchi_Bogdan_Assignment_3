package org.biamn.ds2024.user_microservice.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.user_microservice.dto.user_device.DeviceUserRequestDTO;
import org.biamn.ds2024.user_microservice.mapper.DeviceUserMapper;
import org.biamn.ds2024.user_microservice.model.UserEntity;
import org.biamn.ds2024.user_microservice.repository.DeviceClient;
import org.springframework.stereotype.Service;
import java.util.UUID;

import static org.biamn.ds2024.user_microservice.exceptions.model.ExceptionCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceUserService {

    private final DeviceClient deviceClient;
    private final DeviceUserMapper deviceUserMapper;

    public void save(UserEntity userEntity) {
        DeviceUserRequestDTO deviceUserRequestDTO = DeviceUserRequestDTO.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
        System.out.println("Saving user in device microservice");
        try {
            deviceClient.saveDeviceUser(deviceUserRequestDTO);
        }
        catch (FeignException.FeignServerException e){
            log.error("Error while saving user device: {}", e.getMessage());
            throw new RuntimeException("Error while saving user device");
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
            throw new RuntimeException(ERR003_DEVICE_MICROSERVICE_DOWN.getMessage());
        }
    }

    public void deleteById(UUID userId) {
        try {
            deviceClient.deleteById(userId);
        }
        catch (FeignException.FeignServerException e){
            log.error("Error while deleting user device: {}", e.getMessage());
            throw new RuntimeException("Error while deleting user device");
        }
        catch (RuntimeException e){
            throw new RuntimeException(ERR003_DEVICE_MICROSERVICE_DOWN.getMessage());
        }
    }

}
