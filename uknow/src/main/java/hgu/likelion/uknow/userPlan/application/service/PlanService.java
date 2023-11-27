package hgu.likelion.uknow.userPlan.application.service;

import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.lectureLike.domain.repository.LectureLikeRepository;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.domain.repository.UserRepository;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import hgu.likelion.uknow.userPlan.domain.repository.PlanTableRepository;
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

    @Transactional
    public Long addPlanTable(PlanTableDto dto, String studentId){

        //Category category = categoryRepository.findById(dto.getCategory_id()).orElseThrow(() -> new IllegalArgumentException("no such Category"));
        //Question question = questionRepository.findById(question_id).orElseThrow(() -> new IllegalArgumentException("no such room"));
        User user = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("no such room"));

        PlanTable newPlanTable = planTableRepository.save(PlanTable.toPlanTable(dto, user));

        return newPlanTable.getId();
    }
}
