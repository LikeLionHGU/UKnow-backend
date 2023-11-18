package hgu.likelion.uknow.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.dto.response.HisnetResponse;
import hgu.likelion.uknow.service.HisnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HisnetLoginController {
    private final HisnetService hisnetService;

    @PostMapping("/login")
    public ResponseEntity<String> hisnetLogin(@RequestBody HisnetRequest hisnetRequest) {
        String session = hisnetService.getSession(hisnetRequest);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/get/{session}")
    public ResponseEntity<String> getStudentInfo(@PathVariable String session) {
        String userInfo = hisnetService.getUserInfo(session);

        return ResponseEntity.ok(userInfo);
    }
}
