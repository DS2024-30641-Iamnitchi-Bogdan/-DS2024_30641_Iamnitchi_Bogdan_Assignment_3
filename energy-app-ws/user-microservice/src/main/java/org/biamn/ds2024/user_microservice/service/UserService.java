package org.biamn.ds2024.user_microservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.biamn.ds2024.user_microservice.dto.CollectionResponseDTO;
import org.biamn.ds2024.user_microservice.dto.PageRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserRequestDTO;
import org.biamn.ds2024.user_microservice.dto.user.UserResponseDTO;
import org.biamn.ds2024.user_microservice.exceptions.model.ExceptionCode;
import org.biamn.ds2024.user_microservice.exceptions.model.ResourceNotFoundException;
import org.biamn.ds2024.user_microservice.mapper.UserMapper;
import org.biamn.ds2024.user_microservice.model.UserEntity;
import org.biamn.ds2024.user_microservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final DeviceUserService deviceUserService;
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${spring.application.name:BACKEND}")
    private String applicationName;

    @Transactional
    public UserResponseDTO save(UserRequestDTO userRequestDTO) {
        UUID userId = keycloakService.createUser(userRequestDTO);
        UserEntity userToBeAdded = UserEntity.builder()
                .id(userId)
                .firstName(userRequestDTO.getFirstName())
                .lastName(userRequestDTO.getLastName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .role(userRequestDTO.getRole())
                .build();

        try {
            deviceUserService.save(userToBeAdded);
        } catch (Exception e) {
            keycloakService.deletById(userId);
            throw new RuntimeException("Error while saving user in device microservice");
        }
        try {
            UserEntity userAdded = userRepository.save(userToBeAdded);
            return userMapper.userEntityToUserResponseDTO(userAdded);
        } catch (Exception e) {
            keycloakService.deletById(userId);
            deviceUserService.deleteById(userId);
            throw new RuntimeException("Error while saving user in user microservice");
        }
    }

    public UserResponseDTO updateById(UUID userId, UserRequestDTO userRequestDTO){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_USER_NOT_FOUND.getMessage(),
                        userId
                )));
        UserRequestDTO oldUser = userMapper.userEntityToUserRequestDTO(userEntity);
        UserEntity oldUserEntity = userMapper.userRequestDTOToUserEntity(oldUser);

        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setRole(userRequestDTO.getRole());

        keycloakService.updateUser(userId, userRequestDTO);
        try {
            deviceUserService.save(userEntity);
        } catch (Exception e) {
            keycloakService.updateUser(userId, oldUser);
            throw new RuntimeException("Error while updating user in device microservice");
        }
        try {
            UserEntity updatedUserEntity = userRepository.save(userEntity);
            return userMapper.userEntityToUserResponseDTO(updatedUserEntity);

        } catch (Exception e) {
            keycloakService.updateUser(userId, oldUser);
            deviceUserService.save(oldUserEntity);
            throw new RuntimeException("Error while updating user in device microservice");
        }
    }

    public void deleteById(UUID userId){
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_USER_NOT_FOUND.getMessage(),
                        userId
                )));
        UserRequestDTO userRequestDTO = userMapper.userEntityToUserRequestDTO(userEntity);

        keycloakService.deletById(userId);
        try {
            deviceUserService.deleteById(userId);
        } catch (Exception e) {
            keycloakService.createUser(userRequestDTO);
            throw new RuntimeException("Error while deleting user in device microservice");
        }
        try {
            userRepository.deleteById(userId);
        } catch (Exception e) {
            keycloakService.updateUser(userId, userRequestDTO);
            deviceUserService.save(userEntity);
            throw new RuntimeException("Error while deleting user in device microservice");
        }
    }

    public List<UserResponseDTO> findAll() {
        log.info("Getting all users for {}", applicationName);
        List<UserEntity> userEntityList = userRepository.findAll();
        return userMapper.userEntityListToUserResponseDTOList(userEntityList);
    }

    public UserResponseDTO findById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::userEntityToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_USER_NOT_FOUND.getMessage(),
                        userId
                )));
    }

    public UserResponseDTO findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
                .map(userMapper::userEntityToUserResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        ExceptionCode.ERR001_USER_NOT_FOUND.getMessage(),
                        userEmail
                )));
    }

    public List<UserResponseDTO> findByFirstName(String firstName){
        return userRepository.findByFirstName(firstName).stream()
                .map(userMapper::userEntityToUserResponseDTO)
                .collect(Collectors.toList());
    }

    public List<UserResponseDTO> findByLastName(String lastName){
        return userRepository.findByFirstName(lastName).stream()
                .map(userMapper::userEntityToUserResponseDTO)
                .collect(Collectors.toList());
    }

    public CollectionResponseDTO<UserResponseDTO> findAllPaged(PageRequestDTO page) {
        Page<UserEntity> userEntityList = userRepository.findAll(PageRequest.of(
                page.getPageNumber(),
                page.getPageSize()
        ));
        List<UserResponseDTO> users = userMapper.userEntityListToUserResponseDTOList(userEntityList.getContent());

        return new CollectionResponseDTO<>(users, userEntityList.getTotalElements());
    }
}
