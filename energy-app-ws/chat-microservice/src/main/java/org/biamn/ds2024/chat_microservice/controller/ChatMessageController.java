package org.biamn.ds2024.chat_microservice.controller;

import lombok.RequiredArgsConstructor;
import org.biamn.ds2024.chat_microservice.model.chat.ChatMessage;
import org.biamn.ds2024.chat_microservice.service.ChatMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ) {
        ChatMessage savedMessage = chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/read")
    public void processReadMessage(
            @Payload ChatMessage chatMessage
    ) {
        chatMessage.setRead(true);
        chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getSenderId(),
                "/queue/messages",
                chatMessage
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(senderId, recipientId));
    }

    //delete all messages
    @DeleteMapping("/messages")
    public ResponseEntity<Void> deleteAllMessages() {
        chatMessageService.deleteAllMessages();
        return ResponseEntity.noContent().build();
    }
}
