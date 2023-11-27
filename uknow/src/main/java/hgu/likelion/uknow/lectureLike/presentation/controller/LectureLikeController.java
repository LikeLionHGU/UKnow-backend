package hgu.likelion.uknow.lectureLike.presentation.controller;

import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lectureLike.application.service.LectureLikeService;
import hgu.likelion.uknow.user.application.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureLikeController {

    private final LectureLikeService subjectFavoritesService;
    private final HisnetService hisnetService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @GetMapping("user/subjectFavorites/{lecture_id}")
    public ResponseEntity<Boolean> like(@PathVariable Long lecture_id, HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String s = userInfoList.get(0).get(1).get(1);

        System.out.println(s);

        Boolean result = subjectFavoritesService.saveLike(lecture_id, s);
        System.out.println(result);
        return ResponseEntity.ok(result);
    }
}
