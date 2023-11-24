package hgu.likelion.uknow.hisnet.service;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HisnetService {
    private final RestTemplate restTemplate;

    @Autowired
    public HisnetService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Transactional
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

        return session;
    }

    @Transactional
    public String getUserInfo(String session) {
        String url = "https://hisnet.handong.edu/prof/graduate/PGRA123S_gong.php?gubun=hak";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(
                MediaType.TEXT_HTML,
                MediaType.APPLICATION_XHTML_XML,
                MediaType.APPLICATION_XML,
                new MediaType("image", "avif"),
                MediaType.valueOf("image/webp"),
                new MediaType("image", "apng"),
                MediaType.ALL
        ));
        headers.add("Accept-Encoding", "gzip, deflate, br");
        headers.add("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7");
        headers.setConnection("keep-alive");
        headers.add("Cookie", "PHPSESSID=" + session);
        headers.add("Host", "hisnet.handong.edu");
        headers.add("Referer", "https://hisnet.handong.edu/haksa/graduate/HGRA120M.php");
        headers.add("Sec-Ch-Ua", "\"Google Chrome\";v=\"117\", \"Not;A=Brand\";v=\"8\", \"Chromium\";v=\"117\"");
        headers.add("Sec-Ch-Ua-Mobile", "?0");
        headers.add("Sec-Ch-Ua-Platform", "\"macOS\"");
        headers.add("Sec-Fetch-Dest", "frame");
        headers.add("Sec-Fetch-Mode", "navigate");
        headers.add("Sec-Fetch-Site", "same-origin");
        headers.add("Sec-Fetch-User", "?1");
        headers.add("Upgrade-Insecure-Requests", "1");
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36");

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        Charset charset = Charset.forName("EUC-KR");

        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        String decodedResponse = new String(response.getBody(), charset);

        return decodedResponse;
    }

    @Transactional
    public List<List<List<String>>> parseData(String htmlCode) {
        Document document = Jsoup.parse(htmlCode);
        List<List<List<String>>> result = new ArrayList<>();

        // "#att_list" ID를 가진 모든 테이블을 찾아 순회합니다.
        Elements tables = document.select("table#att_list");
        for (Element table : tables) {
            List<List<String>> currentTable = new ArrayList<>();
            Elements rows = table.select("tbody tr");

            // 각 행(tr)을 순회하면서 셀(td)의 데이터를 추출합니다.
            for (Element row : rows) {
                List<String> rowData = new ArrayList<>();
                Elements cells = row.select("td");
                for (Element cell : cells) {
                    rowData.add(cell.text().trim());
                }
                currentTable.add(rowData);
            }
            result.add(currentTable);
        }

        return result;
    }
}
