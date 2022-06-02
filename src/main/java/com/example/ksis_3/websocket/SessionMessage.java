package com.example.ksis_3.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionMessage {
    private String userName;
    private String userChoice;
    private String sessionStatus;
}
