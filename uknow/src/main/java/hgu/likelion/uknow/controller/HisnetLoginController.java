package hgu.likelion.uknow.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.dto.response.HisnetResponse;
import hgu.likelion.uknow.service.HisnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<List<List<String>>>> getStudentInfo(@PathVariable String session) {
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        return ResponseEntity.ok(userInfoList);
    }

}
