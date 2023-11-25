package hgu.likelion.uknow.lecture.presentation.controller;

import hgu.likelion.uknow.lecture.application.service.LectureService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;


    @PostMapping("/user/get/lecture") // html 파싱 후 Data base에 전체 강의 목록 집어넣는 function
    public ResponseEntity<List<List<String>>> getLectureInfo(@RequestBody String html) {
        List<List<String>> returnValue = lectureService.parseLecture(html);
        lectureService.addLecture(returnValue);

        return ResponseEntity.ok(returnValue);
    }

    @PostMapping("/test")
    public ResponseEntity<Void> test(HttpServletRequest request) {
        System.out.println(request.getSession().getId());
        System.out.println(":::" + SecurityContextHolder.getContext().getAuthentication().getName());
        return ResponseEntity.ok(null);
    }


}
