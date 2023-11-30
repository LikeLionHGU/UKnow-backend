package hgu.likelion.uknow.userPlan.application.service;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.lectureLike.domain.repository.LectureLikeRepository;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.domain.repository.UserRepository;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import hgu.likelion.uknow.userPlan.domain.repository.PlanLectureRepository;
import hgu.likelion.uknow.userPlan.domain.repository.PlanTableRepository;
import hgu.likelion.uknow.userPlan.presentation.response.PlanInfoResponse;
import hgu.likelion.uknow.userPlan.presentation.response.PlanLectureResponse;
import hgu.likelion.uknow.userlecture.application.service.UserLectureService;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.domain.repository.UserLectureRepository;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureLikeRepository lectureLikeRepository;
    private final PlanTableRepository planTableRepository;
    private final PlanLectureRepository planLectureRepository;
    private final UserLectureRepository userLectureRepository;

    @Transactional
    public Long addPlanTable(PlanTableDto dto, String studentId) {

        //Category category = categoryRepository.findById(dto.getCategory_id()).orElseThrow(() -> new IllegalArgumentException("no such Category"));
        //Question question = questionRepository.findById(question_id).orElseThrow(() -> new IllegalArgumentException("no such room"));
        User user = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("no such room"));

        PlanTable newPlanTable = planTableRepository.save(PlanTable.toPlanTable(dto, user));

        return newPlanTable.getId();
    }

    @Transactional
    public Boolean addPlanLecture(Long planTable_id, Long lecture_id) {
        System.out.println("===>1");
        PlanTable planTable = planTableRepository.findById(planTable_id).orElseThrow(() -> new IllegalArgumentException("no such scrapFolder"));
        System.out.println("===>2");
        Lecture lecture = lectureRepository.findById(Long.toString(lecture_id)).orElseThrow(() -> new IllegalArgumentException("no such question"));


        String code = lecture.getCode();

        List<PlanLecture> findPlanLecture = planLectureRepository.findByPlanTableIdAndLectureId(planTable.getId(), lecture.getId());
        System.out.println(planTable.getTableName() + " + " + lecture.getName());


        if (findPlanLecture.isEmpty()) {
            // 중복이 아니라면
            PlanLecture setPlanLecture = PlanLecture.toPlanLecture(planTable, lecture);
            planLectureRepository.save(setPlanLecture);

            // 위에 정보 업데이트를 통해서 각 분류에서 몇학점 남았는지 다시 계산해서 리스폰스로 리턴 해야함.

            return true;
        } else {
            // 중복이라면 백에서 처리 해주나요? 프론트에서 처리해주나욤

            return false;

        }

    }
    @Transactional
    public List<PlanInfoResponse> planInfo(String studentId){

        List<PlanInfoResponse> PlanInfoResponseList = new ArrayList<>();
        List<PlanTable> userPlanTablelist = planTableRepository.findByStudentId(studentId);

        for(PlanTable t : userPlanTablelist){

            PlanInfoResponse planInfoResponse = new PlanInfoResponse();


            List<PlanLecture> findPlanLecturelistInTable = planLectureRepository.findByPlanTableId(t.getId());
            List<PlanLectureResponse> planLecturelist = findPlanLecturelistInTable.stream().map(PlanLectureResponse::toResponse).collect(Collectors.toList());

            planInfoResponse.setTableId(t.getId());
            planInfoResponse.setTableName(t.getTableName());
            planInfoResponse.setUserPlanLectureResponse(planLecturelist);
            PlanInfoResponseList.add(planInfoResponse);
        }


        return  PlanInfoResponseList;
    }
}