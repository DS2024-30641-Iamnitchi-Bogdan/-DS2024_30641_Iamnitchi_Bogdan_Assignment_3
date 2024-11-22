package org.biamn.ds2024.chat_microservice.service;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.chat_microservice.model.Status;
import org.biamn.ds2024.chat_microservice.model.User;
import org.biamn.ds2024.chat_microservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void saveUser(User user) {
        user.setStatus(Status.ONLINE);
        repository.save(user);
    }

    public void disconnect(User user) {
        var storedUser = repository.findById(user.getNickName()).orElseThrow();
        storedUser.setStatus(Status.OFFLINE);
        repository.save(storedUser);
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByStatus(Status.ONLINE);
    }
}
