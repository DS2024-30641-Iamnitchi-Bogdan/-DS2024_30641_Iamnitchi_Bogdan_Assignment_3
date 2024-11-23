package org.biamn.ds2024.chat_microservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
public class User {

    @Id
    private String nickName;
    private String fullName;
    private Status status;
}
