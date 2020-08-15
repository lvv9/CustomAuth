package me.liuweiqiang.customauth.common.config.security;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TicketAuthFilter extends AbstractAuthenticationProcessingFilter {

    public TicketAuthFilter(String path) {
        super(new AntPathRequestMatcher(path, "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {
        if (!Objects.equals("POST", request.getMethod())) {
            throw new AuthenticationServiceException(
                    request.getMethod() + "auth not supported"
            );
        } else {
            TicketAuthToken authToken = new TicketAuthToken(obtainArgStringMap(request));
            authToken.setDetails(authenticationDetailsSource.buildDetails(request));
            return this.getAuthenticationManager().authenticate(authToken);
        }
    }

    private Map<String, String> obtainArgStringMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        for (String key: request.getParameterMap().keySet()) {
            map.put(key, request.getParameter(key));
        }
        return map;
    }
}
