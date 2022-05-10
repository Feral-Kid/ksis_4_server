package com.example.ksis_3.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionMessage {
    private String userName;
    private String userChoice;
    private String sessionStatus;
}
