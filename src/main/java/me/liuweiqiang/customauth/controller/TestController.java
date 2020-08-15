package me.liuweiqiang.customauth.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@ConditionalOnProperty(value = "ticket.service", havingValue = "false")
public class TestController {

    @RequestMapping(method = RequestMethod.GET)
    public String test() {
        return "User:" + SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
