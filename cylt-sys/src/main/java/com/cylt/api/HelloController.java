package com.cylt.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/getHello")
    public String getHello(){
        return "hello from nacos provider!";
    }

}
