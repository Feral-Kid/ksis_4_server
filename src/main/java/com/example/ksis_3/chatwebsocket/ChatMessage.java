package com.example.ksis_3.chatwebsocket;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatMessage {

    String type;

    String userName;

    String userId;

    String userMessage;

    String groupId;
}
