package hgu.likelion.uknow.userPlan.presentation.controller;

import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lectureLike.application.service.LectureLikeService;
import hgu.likelion.uknow.user.application.service.UserService;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.application.service.PlanService;
import hgu.likelion.uknow.userPlan.presentation.request.PlanTableRequest;
import hgu.likelion.uknow.userPlan.presentation.response.NeedCreditInfoResponse;
import hgu.likelion.uknow.userPlan.presentation.response.PlanInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class PlanController {

    private final JwtProvider jwtProvider;
    private final LectureLikeService subjectFavoritesService;
    private final HisnetService hisnetService;
    private final UserService userService;
    private final PlanService planService;

    @PostMapping("/addPlanTable")
    public ResponseEntity<Long> addPlanTable(@RequestBody PlanTableRequest planTablerequest, HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        System.out.println(studentId);
        
        Long savedId = planService.addPlanTable(PlanTableDto.toAddScrapFolder(planTablerequest), studentId);
        return ResponseEntity.ok(savedId);
    }


    @GetMapping("/addPlanLecture/{planTable_id}/{lecture_id}")
    public ResponseEntity<Boolean> addPlanLecture(@PathVariable Long planTable_id, @PathVariable Long lecture_id) {

        Boolean result = planService.addPlanLecture(planTable_id, lecture_id);
        //System.out.println(result);
        //String s = "리턴값으로 채워야 하는 학점 수, 과목 정보도줘야 하나요?";
        return ResponseEntity.ok(result);
    }

    @GetMapping("/PlanInfo")
    public ResponseEntity<List<PlanInfoResponse>> planInfo(HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);


        List<PlanInfoResponse> s = planService.planInfo(studentId);


        return ResponseEntity.ok(s);
    }

    @GetMapping("/needCreditInfo")
    public ResponseEntity<List<NeedCreditInfoResponse>> planCreditInfo(HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        List<NeedCreditInfoResponse> n = planService.needCreditInfo(studentId);


        return ResponseEntity.ok(n);
    }


}
