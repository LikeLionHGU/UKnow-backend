package hgu.likelion.uknow.lectureLike.presentation.controller;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lectureLike.application.service.LectureLikeService;
import hgu.likelion.uknow.lectureLike.presentation.response.LikeLectureResponse;
import hgu.likelion.uknow.user.application.service.UserService;
import hgu.likelion.uknow.userPlan.presentation.response.PlanInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class LectureLikeController {

    private final LectureLikeService subjectFavoritesService;
    private final HisnetService hisnetService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("/subjectFavorites/{lecture_id}/{enum}")
    public ResponseEntity<Boolean> like(@PathVariable Long lecture_id, @PathVariable("enum") LectureType lectureType, HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        System.out.println(studentId);

        Boolean result = subjectFavoritesService.saveLike(lecture_id, studentId, lectureType);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/likeLectureList")
    public ResponseEntity<List<LikeLectureResponse>> like(HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        System.out.println(studentId);

        System.out.println("=====>1");
        List<LikeLectureResponse> result = subjectFavoritesService.getLikeLectureList(studentId);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }

}
