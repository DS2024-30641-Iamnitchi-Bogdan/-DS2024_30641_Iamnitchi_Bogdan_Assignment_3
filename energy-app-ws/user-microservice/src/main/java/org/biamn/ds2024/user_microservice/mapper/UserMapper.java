package org.biamn.ds2024.user_microservice.mapper;

import org.biamn.ds2024.user_microservice.dto.user.UserRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserResponseDTO;
import org.biamn.ds2024.user_microservice.model.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO userEntityToUserResponseDTO(UserEntity userEntity);

    UserRequestDTO userEntityToUserRequestDTO(UserEntity userEntity);

    List<UserResponseDTO> userEntityListToUserResponseDTOList(List<UserEntity> userEntityList);

    UserEntity userRequestDTOToUserEntity(UserRequestDTO userRequestDTO);
}
