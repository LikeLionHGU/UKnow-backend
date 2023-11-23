package hgu.likelion.uknow.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.dto.request.MemberRequest;
import hgu.likelion.uknow.dto.response.MemberResponse;
import hgu.likelion.uknow.service.HisnetService;
import hgu.likelion.uknow.service.LectureService;
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

    @PostMapping("/login") // return value -> studentId and session
    public ResponseEntity<MemberResponse> hisnetLogin(@RequestBody HisnetRequest hisnetRequest) {
        String session = hisnetService.getSession(hisnetRequest);
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        String studentId = userInfoList.get(0).get(1).get(1);
        boolean isSignUp = memberService.isSignUp(studentId);

        if(isSignUp) {
            MemberResponse memberResponse = MemberResponse.toResponse(studentId, session);

            return ResponseEntity.ok(memberResponse);
        } else {
            memberService.addUser(userInfoList);
            MemberResponse memberResponse = MemberResponse.toResponse(studentId, session);

            return ResponseEntity.ok(memberResponse);
        }
    }

    @PostMapping("/get/info") // 학생에 대한 정보 가지고 오는 function
    public ResponseEntity<List<List<List<String>>>> getStudentInfo(@RequestBody MemberRequest memberRequest) {
        String userInfo = hisnetService.getUserInfo(memberRequest.getSession());
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        return ResponseEntity.ok(userInfoList);
    }

}
