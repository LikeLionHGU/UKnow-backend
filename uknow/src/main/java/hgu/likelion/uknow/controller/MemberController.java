package hgu.likelion.uknow.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.service.HisnetService;
import hgu.likelion.uknow.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final HisnetService hisnetService;
    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> hisnetLogin(@RequestBody HisnetRequest hisnetRequest) {
        String session = hisnetService.getSession(hisnetRequest);
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        String studentId = userInfoList.get(0).get(1).get(1);
        boolean isSignUp = memberService.isSignUp(studentId);

        if(isSignUp) {
            return ResponseEntity.ok(studentId);
        } else {
            memberService.addUser(userInfoList);

            return ResponseEntity.ok(studentId);
        }
    }

    @PostMapping("/get/{session}")
    public ResponseEntity<List<List<List<String>>>> getStudentInfo(@PathVariable String session) {
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        return ResponseEntity.ok(userInfoList);
    }

}
