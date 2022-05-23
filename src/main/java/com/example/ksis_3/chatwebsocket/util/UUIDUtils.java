package com.example.ksis_3.chatwebsocket.util;

import com.example.ksis_3.exception.NotValidUUIDException;

import java.util.UUID;

public class UUIDUtils {
    public static UUID getUUIDFromString(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new NotValidUUIDException(String.format("Value = %s is not UUID", uuid));
        }
    }
}
