package hgu.likelion.uknow.userPlan.presentation.controller;

import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lectureLike.application.service.LectureLikeService;
import hgu.likelion.uknow.user.application.service.UserService;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.application.service.PlanService;
import hgu.likelion.uknow.userPlan.presentation.request.PlanTableRequest;
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

    @GetMapping("/PlanCreditInfo/")
    public ResponseEntity<Boolean> planCreditInfo(HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        // 아이디에 맞는 모든 테이블을 불러옴.
        //각 테이블에 있는 모든 과목 정보를 불러옴.
        //유저랙처 리스폰에 유저렉쳐와, 계획렉쳐를 다 넣음.
        //다 넣은 유저랙쳐들로 토탈을 계산함.
        //필요한정보만


        return ResponseEntity.ok(null);
    }


}
