package me.liuweiqiang.customauth.common.config.security;

import me.liuweiqiang.customauth.controller.base.ResponseCode;
import me.liuweiqiang.customauth.controller.base.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TicketSucessHandler implements AuthenticationSuccessHandler {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest var1, HttpServletResponse var2, Authentication var3) throws
            IOException {
        var2.getWriter().write(mapper.writeValueAsString(ResponseResult.e(ResponseCode.SIGN_IN_OK)));
    }
}
