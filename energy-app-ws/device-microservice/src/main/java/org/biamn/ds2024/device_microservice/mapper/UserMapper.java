package org.biamn.ds2024.device_microservice.mapper;

import org.biamn.ds2024.device_microservice.dto.user_device.UserRequestDTO;
import org.biamn.ds2024.device_microservice.dto.user_device.UserResponseDTO;
import org.biamn.ds2024.device_microservice.model.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO userEntityToUserResponseDTO(UserEntity userEntity);

    List<UserResponseDTO> userDeviceEntityListToUserResponseDTOList(List<UserEntity> userEntityList);

    UserEntity userRequestDTOToUserEntity(UserRequestDTO userRequestDTO);
}
