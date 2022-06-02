package com.example.ksis_3.chatwebsocket.util;

import com.example.ksis_3.exception.ParseException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Field;
import java.util.Objects;

public class MessageParser {

    public static <T> T parse(String message, Class<T> messageClass, Gson gson) {
        try {
            T parsedMessage = gson.fromJson(message, messageClass);
            Field[] fields = messageClass.getDeclaredFields();
            for (Field field: fields) {
                field.setAccessible(true);
                Object object = field.get(parsedMessage);
                Objects.requireNonNull(object);
            }
            return parsedMessage;
        } catch (JsonSyntaxException | NullPointerException e) {
            throw new ParseException(String.format("Can not parse message: %s to class %s", message, messageClass.getName()));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
