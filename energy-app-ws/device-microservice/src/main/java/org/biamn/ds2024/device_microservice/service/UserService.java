package org.biamn.ds2024.device_microservice.service;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.device_microservice.dto.user_device.UserRequestDTO;
import org.biamn.ds2024.device_microservice.dto.user_device.UserResponseDTO;
import org.biamn.ds2024.device_microservice.exceptions.exception.model.ExceptionCode;
import org.biamn.ds2024.device_microservice.exceptions.exception.model.ResourceNotFoundException;
import org.biamn.ds2024.device_microservice.mapper.UserMapper;
import org.biamn.ds2024.device_microservice.model.UserEntity;
import org.biamn.ds2024.device_microservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    public final UserRepository userRepository;

    public UserResponseDTO findById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::userEntityToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(),
                        userId
                )));
    }

    public List<UserResponseDTO> findAll() {
        List<UserEntity> userEntityList = userRepository.findAll();
        return userMapper.userDeviceEntityListToUserResponseDTOList(userEntityList);
    }

    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        UserEntity userToBeAdded = userMapper.userRequestDTOToUserEntity(userRequestDTO);
        UserEntity userAdded = userRepository.save(userToBeAdded);
        return userMapper.userEntityToUserResponseDTO(userAdded);
    }

    public UserResponseDTO updateById(UUID userId, UserRequestDTO userRequestDTO){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(),
                        userId
                )));
        userEntity.setId(userId);
        userEntity.setName(userRequestDTO.getName());
        userEntity.setEmail(userRequestDTO.getEmail());

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        return userMapper.userEntityToUserResponseDTO(updatedUserEntity);
    }

    public void deleteById(UUID userId){
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException(String.format(
                    ExceptionCode.ERR003_USER_NOT_FOUND.getMessage(), userId));
        }
        userRepository.deleteById(userId);
    }

}
