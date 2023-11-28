package hgu.likelion.uknow.lecture.presentation.controller;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lecture.application.service.LectureService;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;
    private final JwtProvider jwtProvider;


    @PostMapping("/user/get/lecture") // html 파싱 후 Data base에 전체 강의 목록 집어넣는 function
    public ResponseEntity<List<List<String>>> getLectureInfo(@RequestBody String html) {
        List<List<String>> returnValue = lectureService.parseLecture(html);
        lectureService.addLecture(returnValue);

        return ResponseEntity.ok(returnValue);
    }

    @GetMapping("/user/get/lecture/{name}") // 검색 기능 구현
    public ResponseEntity<List<LectureResponse>> getOneLecture(@PathVariable("name") String name) {
        List<LectureResponse> lectureResponseList = lectureService.getLectureByName(name);



        return ResponseEntity.ok(lectureResponseList);
    }

    @GetMapping("/user/take/{enum}")
    public ResponseEntity<List<LectureResponse>> haveToTake(@PathVariable("enum") LectureType lectureType, HttpServletRequest request) {
        String token = jwtProvider.resolveToken(request);
        String studentId = jwtProvider.getAccount(token);

        List<LectureResponse> lectureResponseList = lectureService.haveToTake(lectureType, studentId);
        return ResponseEntity.ok(lectureResponseList);
    }


}
