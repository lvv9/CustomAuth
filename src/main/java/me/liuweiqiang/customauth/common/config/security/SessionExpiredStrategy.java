package me.liuweiqiang.customauth.common.config.security;

import me.liuweiqiang.customauth.controller.base.ResponseCode;
import me.liuweiqiang.customauth.controller.base.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SessionExpiredStrategy implements SessionInformationExpiredStrategy {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent var1) throws IOException {
        HttpServletResponse httpServletResponse = var1.getResponse();
        httpServletResponse.getWriter().write(mapper.writeValueAsString(ResponseResult.e(ResponseCode.NOT_SING_IN)));
    }
}
