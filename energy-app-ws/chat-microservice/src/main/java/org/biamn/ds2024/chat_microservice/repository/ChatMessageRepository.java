package org.biamn.ds2024.chat_microservice.repository;

import org.biamn.ds2024.chat_microservice.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByChatRoomId(String string);
}
