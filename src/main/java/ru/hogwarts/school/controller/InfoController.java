package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class InfoController {
    @Value("${server.port}")
    private int serverPort;

    public InfoController() {}

    @GetMapping("/port")
    public int getPort() {
        return serverPort;
    }
}
