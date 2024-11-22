package org.biamn.ds2024.user_microservice.mapper;

import org.biamn.ds2024.user_microservice.dto.user_device.DeviceUserRequestDTO;
import org.biamn.ds2024.user_microservice.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceUserMapper {

    DeviceUserRequestDTO userEntityToDeviceUserRequest(UserEntity userEntity);
}
