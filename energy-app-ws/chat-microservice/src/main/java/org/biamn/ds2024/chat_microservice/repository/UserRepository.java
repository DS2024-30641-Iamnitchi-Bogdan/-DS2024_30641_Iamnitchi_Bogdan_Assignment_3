package org.biamn.ds2024.chat_microservice.repository;

import org.biamn.ds2024.chat_microservice.model.Status;
import org.biamn.ds2024.chat_microservice.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {

    List<User> findAllByStatus(Status status);
}
