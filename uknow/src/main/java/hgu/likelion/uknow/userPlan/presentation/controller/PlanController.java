package hgu.likelion.uknow.userPlan.presentation.controller;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.hisnet.service.HisnetService;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lectureLike.application.service.LectureLikeService;
import hgu.likelion.uknow.user.application.service.UserService;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.application.service.PlanService;
import hgu.likelion.uknow.userPlan.presentation.request.PlanLectureRequest;
import hgu.likelion.uknow.userPlan.presentation.request.PlanTableRequest;
import hgu.likelion.uknow.userPlan.presentation.response.NeedCreditInfoResponse;
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
public class PlanController {

    private final JwtProvider jwtProvider;
    private final LectureLikeService subjectFavoritesService;
    private final HisnetService hisnetService;
    private final UserService userService;
    private final PlanService planService;

    @PostMapping("/addPlanTable")
    public ResponseEntity<Long> addPlanTable(@RequestBody PlanTableRequest planTablerequest,  HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        System.out.println(studentId);
        
        Long savedId = planService.addPlanTable(PlanTableDto.toAddScrapFolder(planTablerequest), studentId);
        return ResponseEntity.ok(savedId);
    }

    @GetMapping("/getPlanAllInfo")
    public ResponseEntity<List<Object>> planAllInfo(HttpServletRequest request) {

        String session = userService.getSession(jwtProvider.resolveToken(request));


        String userInfo = hisnetService.getUserInfo(session);
        List<List<List<String>>> userInfoList = hisnetService.parseData(userInfo);
        String studentId = userInfoList.get(0).get(1).get(1);

        System.out.println(studentId);

        List<Object> savedId = planService.planAllInfo(studentId);
        return ResponseEntity.ok(savedId);
    }



    @PostMapping("/addPlanLecture/{planTable_id}/{planLecture_id}/{enum_type}")
    public ResponseEntity<Boolean> addPlanLecture(@PathVariable Long planTable_id, @PathVariable Long planLecture_id, @PathVariable LectureType enum_type){

        Boolean result = planService.addPlanLecture(planTable_id,planLecture_id, enum_type);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/deletePlanLecture/{planTable_id}/{planLecture_id}")
    public ResponseEntity<Boolean> deletePlanLecture(@PathVariable Long planTable_id, @PathVariable Long planLecture_id) {

        Boolean result = planService.deletePlanLecture(planTable_id,planLecture_id);
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
