package org.biamn.ds2024.chat_microservice.model.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class ChatRoom {

    @Id
    private String id;
    private String chatRoomId;
    private String senderId;
    private String recipientId;
}
