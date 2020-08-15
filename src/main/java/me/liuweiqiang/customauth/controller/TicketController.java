package me.liuweiqiang.customauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@ConditionalOnProperty(value = "loginpage", havingValue = "true")
public class TicketController {

    @Value("${ticket.path}")
    private String path;

    @RequestMapping("${ticket.page}")
    public String ticket(Model model) {
        model.addAttribute("path", path);
        return "login";
    }
}
