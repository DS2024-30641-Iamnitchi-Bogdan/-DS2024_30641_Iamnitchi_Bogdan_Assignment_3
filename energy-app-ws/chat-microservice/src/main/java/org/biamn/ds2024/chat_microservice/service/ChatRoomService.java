package org.biamn.ds2024.chat_microservice.service;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.chat_microservice.model.chat.ChatRoom;
import org.biamn.ds2024.chat_microservice.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository repository;

    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExist) {
        return repository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatRoomId)
                .or(() -> {
                    if (createNewRoomIfNotExist) {
                        var chatRoomId = createChatRoom(senderId, recipientId);
                        System.out.println("Chat room created: " + chatRoomId);
                        return Optional.of(chatRoomId);
                    }
                    return Optional.empty();
                });
    }

    private String createChatRoom(String senderId, String recipientId) {
        var chatRoomId = String.format("%s_%s", senderId, recipientId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatRoomId(chatRoomId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();
        repository.save(senderRecipient);
        repository.save(recipientSender);
        return chatRoomId;
    }
}
