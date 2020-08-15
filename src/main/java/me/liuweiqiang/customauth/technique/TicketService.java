package me.liuweiqiang.customauth.technique;

import me.liuweiqiang.customauth.common.exception.TicketException;

import java.util.Map;

public interface TicketService {

    void verifyTicket(Map<String, String> args) throws TicketException;
}
