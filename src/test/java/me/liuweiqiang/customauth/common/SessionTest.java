package me.liuweiqiang.customauth.common;

import me.liuweiqiang.customauth.common.exception.TicketException;
import me.liuweiqiang.customauth.controller.base.ResponseCode;
import me.liuweiqiang.customauth.controller.base.ResponseResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.liuweiqiang.customauth.technique.TicketService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SessionTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    private int port;
    @Value("${ticket.path}")
    private String path;
    @MockBean
    private TicketService ticketService;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String clientId = "user1";

    public ResponseEntity<String> login(String arg) {
        String url = "http://localhost:" + port + path;

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("ticket", arg);
        map.add("clientid", clientId);
        map.add("serverid", "2");
        map.add("uri", "3");
        map.add("clientip", "4");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response;
    }

    public String getCookie(ResponseEntity<String> response) {
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        logger.debug("Set-Cookie:" + cookies.toString());
        return cookies.get(0);
    }

    @Before
    public void setup() throws Exception {
        Map<String, String> argMap = new HashMap<>();
        argMap.put("ticket", "test002");
        argMap.put("clientid", "1");
        argMap.put("serverid", "2");
        argMap.put("uri", "3");
        argMap.put("clientip", "4");
        Mockito.doThrow(TicketException.class).when(ticketService).verifyTicket(argMap);
    }

    @Test
    public void sessionConcurrencyCase001() throws Exception {
        String cookie = getCookie(login("test001"));
        String url = "http://localhost:" + port + "/test";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        HttpEntity<?> request = new HttpEntity<>(headers);

        String cookie2 = getCookie(login("test001"));

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        ResponseResult<?> responseResult = objectMapper.readValue(response.getBody(), ResponseResult.class);

        logger.info("Cookies: 1){}, 2){}", cookie, cookie2);
        Assert.assertEquals(ResponseCode.NOT_SING_IN.code.intValue(), responseResult.getStatus().intValue());
    }
}
