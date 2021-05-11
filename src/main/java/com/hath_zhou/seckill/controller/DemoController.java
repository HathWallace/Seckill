package com.hath_zhou.seckill.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/hello")//http://localhost:8080/demo/hello
    public String hello(Model model) {
        model.addAttribute("name", "hath_zhou");
        return "hello";
    }
}
