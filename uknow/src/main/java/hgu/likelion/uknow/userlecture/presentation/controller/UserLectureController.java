package hgu.likelion.uknow.userlecture.presentation.controller;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.userlecture.application.service.UserLectureService;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserLectureController {
    private final UserLectureService userLectureService;
    private final JwtProvider jwtProvider;

    @GetMapping("/get/lecture/list") // 전체 list return 해줌
    public ResponseEntity<List<UserLectureResponse>> getAllList(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        String studentId = jwtProvider.getAccount(token);

        List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentId(studentId);

        return ResponseEntity.ok(userLectureResponseList);
    }

    @GetMapping("/get/lecture/subject")
    public ResponseEntity<List<UserLectureTotalResponse>> getTotalList(HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        String studentId = jwtProvider.getAccount(token);
        List<UserLectureTotalResponse> userLectureTotalResponseList = userLectureService.checkStudentLecture(studentId);

        return ResponseEntity.ok(userLectureTotalResponseList);
    }
}
