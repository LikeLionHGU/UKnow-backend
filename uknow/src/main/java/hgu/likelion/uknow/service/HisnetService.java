package hgu.likelion.uknow.service;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class HisnetService {
    private final RestTemplate restTemplate;

    @Autowired
    public HisnetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getSession(HisnetRequest hisnetRequest) {
        String loginUrl = "https://hisnet.handong.edu/login/_login.php";
        String session = null;
        String id = hisnetRequest.getId();
        String password = hisnetRequest.getPassword();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("id", id);
        map.add("password", password);
        map.add("Language", "Korean");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

        List<String> setCookieHeader = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookieHeader != null && !setCookieHeader.isEmpty()) {

            String sessionCookie = setCookieHeader.get(0);

            String[] cookieParts = sessionCookie.split(";");
            String sessionPart = cookieParts[0];

            String[] sessionPair = sessionPart.split("=");
            if (sessionPair.length > 1) {
                session = sessionPair[1];
            }
        }
        System.out.println(session);

        return session;
    }

    public String getStudentData(String session) {

        return null;
    }

    public void parseData(String htmlCode) {
        Document document = Jsoup.parse(htmlCode);
    }
}
