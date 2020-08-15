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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TicketLoginTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @LocalServerPort
    private int port;
    @Value("${ticket.path}")
    private String path;
    @MockBean
    private TicketService ticketService;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper objectMapper = new ObjectMapper();
    private String clientId = "1";

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
    public void case001() {
        String cookie = getCookie(login("test001"));
        String url = "http://localhost:" + port + "/test";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", cookie);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        Assert.assertEquals( "User:" + clientId, response.getBody());
    }

    @Test
    public void case002() throws Exception {
        ResponseResult<?> responseResult = objectMapper.readValue(login("test001").getBody(), ResponseResult.class);
        Assert.assertEquals(ResponseCode.SIGN_IN_OK.code.intValue(), responseResult.getStatus().intValue());
    }

    @Test
    public void case003() throws Exception {
        ResponseResult<?> responseResult = objectMapper.readValue(login("test002").getBody(), ResponseResult.class);
        Assert.assertEquals(ResponseCode.SIGN_IN_FAIL.code.intValue(), responseResult.getStatus().intValue());
    }

    @Test
    public void case004() {
        String url = "http://localhost:" + port + "/test";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(HttpStatus.FORBIDDEN.value(), e.getRawStatusCode());
        }
    }

    @Test
    public void case005() throws Exception {
        String url = "http://localhost:" + port + "/test";

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseResult<?> responseResult = objectMapper.readValue(login("test002").getBody(), ResponseResult.class);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
        } catch (HttpClientErrorException e) {
            Assert.assertEquals(HttpStatus.FORBIDDEN.value(), e.getRawStatusCode());
        }
    }
}
