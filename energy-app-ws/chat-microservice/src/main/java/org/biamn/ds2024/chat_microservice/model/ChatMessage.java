package org.biamn.ds2024.chat_microservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Document
public class ChatMessage {

    @Id
    private String id;
    private String chatRoomId;
    private String senderId;
    private String recipientId;
    private String content;
    private String timestamp;
}
