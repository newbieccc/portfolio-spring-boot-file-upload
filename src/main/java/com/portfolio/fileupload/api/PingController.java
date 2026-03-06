package com.portfolio.fileupload.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {


    @GetMapping("/api/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/api/ping/error")
    public String pingError() {
        throw new IllegalArgumentException("테스트용 에러입니다.");
    }
}
