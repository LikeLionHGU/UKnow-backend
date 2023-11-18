package hgu.likelion.uknow.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.dto.response.HisnetResponse;
import hgu.likelion.uknow.service.HisnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HisnetLoginController {
    private final HisnetService hisnetService;

    @PostMapping("/login")
    public ResponseEntity<HisnetResponse> hisnetLogin(@RequestBody HisnetRequest hisnetRequest) {
        hisnetService.getSession(hisnetRequest);
        return null;
    }
}
