package hgu.likelion.uknow.user.presentation.controller;

import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.user.application.dto.UserJwtDto;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.presentation.request.UserRequest;
import hgu.likelion.uknow.user.presentation.response.UserResponse;
import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.user.application.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final HisnetService hisnetService;
    private final UserService userService;
    private final JwtProvider tokenProvider;

    @PostMapping("/auth/register")
    public ResponseEntity<String> signUp(@RequestBody HisnetRequest hisnetRequest) {
        String session = hisnetService.getSession(hisnetRequest);
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        User user = userService.addUser(userInfoList);

        return ResponseEntity.ok(user.getStudentId());
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponse> hisnetLogin(@RequestBody HisnetRequest hisnetRequest) {
        String session = hisnetService.getSession(hisnetRequest);
        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);

        if (userInfoList != null) { // 히즈넷에서 로그인을 성공적으로 한 경우

            UserResponse userResponse = userService.login(userInfoList, session);

            return ResponseEntity.ok(userResponse);

        } else {
            return ResponseEntity.ok(null); // null이 반환되는 경우는 히즈넷 로그인 실패를 의미
        }
    }

    @PostMapping("/user/get/info") // 학생에 대한 정보 가지고 오는 function
    public ResponseEntity<List<List<List<String>>>> getStudentInfo(@RequestBody UserRequest userRequest) {
        String userInfo = hisnetService.getUserInfo(userRequest.getSession());
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        System.out.println(userInfoList);
        userService.addUserLectureList(userInfoList);

        return ResponseEntity.ok(userInfoList);
    }

}
