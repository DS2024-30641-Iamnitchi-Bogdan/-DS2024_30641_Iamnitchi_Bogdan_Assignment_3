package org.biamn.ds2024.chat_microservice.service;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.chat_microservice.model.chat.ChatMessage;
import org.biamn.ds2024.chat_microservice.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatRoomId = chatRoomService.getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow();
        chatMessage.setChatRoomId(chatRoomId);
        return repository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
        var chatRoomId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatRoomId.map(repository::findByChatRoomId)
                .orElse(new ArrayList<>());
    }
}
