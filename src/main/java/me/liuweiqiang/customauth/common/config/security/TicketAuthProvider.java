package me.liuweiqiang.customauth.common.config.security;

import me.liuweiqiang.customauth.common.exception.TicketException;
import me.liuweiqiang.customauth.technique.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;

public class TicketAuthProvider implements AuthenticationProvider {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private TicketService ticketService;

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public Authentication authenticate(Authentication var1) throws AuthenticationException {
        TicketAuthToken ticketAuthToken = (TicketAuthToken) var1;
        try {
            logger.debug("Token: {}", ticketAuthToken.getToken().entrySet().toString());
            ticketService.verifyTicket(ticketAuthToken.getToken());
        } catch (TicketException e) {
            throw new InternalAuthenticationServiceException("TicketException");
        } catch (Exception e) {
            logger.warn("Ticket warning");
            throw new InternalAuthenticationServiceException("Unknow Exception");
        }

        TicketAuthToken auth = new TicketAuthToken(ticketAuthToken.getToken(),
                AuthorityUtils.commaSeparatedStringToAuthorityList("USER"));
        auth.setDetails(ticketAuthToken.getDetails());
        return auth;
    }

    @Override
    public boolean supports(Class<?> var1) {
        return TicketAuthToken.class.isAssignableFrom(var1);
    }
}
