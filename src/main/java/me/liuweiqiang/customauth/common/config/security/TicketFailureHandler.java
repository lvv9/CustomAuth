package me.liuweiqiang.customauth.common.config.security;

import me.liuweiqiang.customauth.controller.base.ResponseCode;
import me.liuweiqiang.customauth.controller.base.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TicketFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper mapper = new ObjectMapper();

    public void onAuthenticationFailure(HttpServletRequest var1, HttpServletResponse var2, AuthenticationException var3) throws
            IOException {
        var2.getWriter().write(mapper.writeValueAsString(ResponseResult.e(ResponseCode.SIGN_IN_FAIL)));
    }
}
