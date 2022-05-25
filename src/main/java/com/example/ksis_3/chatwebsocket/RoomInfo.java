package com.example.ksis_3.chatwebsocket;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoomInfo {
    String name;
    String UUID;
}
