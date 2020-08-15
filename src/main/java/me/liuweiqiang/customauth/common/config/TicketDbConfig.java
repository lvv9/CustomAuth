package me.liuweiqiang.customauth.common.config;

import me.liuweiqiang.customauth.common.exception.TicketException;
import me.liuweiqiang.customauth.technique.TicketService;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TicketDbConfig {

    @Bean
    @ConditionalOnProperty(value = "ticket.service", havingValue = "false")
    TicketService ticketService() throws TicketException {
        TicketService ticketService = Mockito.mock(TicketService.class);
        Map<String, String> argMap = new HashMap<>();
        argMap.put("ticket", "test002");
        argMap.put("clientid", "1");
        argMap.put("serverid", "2");
        argMap.put("uri", "3");
        argMap.put("clientip", "4");
        Mockito.doThrow(TicketException.class).when(ticketService).verifyTicket(argMap);
        return ticketService;
    }
}
