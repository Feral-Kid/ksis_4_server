package com.example.ksis_3;

import com.google.gson.Gson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Ksis3Application {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("lol");
        list.add("prcol");
        Gson gson = new Gson();
        String json = gson.toJson(list);
        List<String> list1 = gson.fromJson(json, List.class);
        SpringApplication.run(Ksis3Application.class, args);
    }

}
