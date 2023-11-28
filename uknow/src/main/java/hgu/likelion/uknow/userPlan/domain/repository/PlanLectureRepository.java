package hgu.likelion.uknow.userPlan.domain.repository;

import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanLectureRepository extends JpaRepository<PlanLecture, Long> {

    List<PlanLecture> findByPlanTableIdAndLectureId(Long planTable_id, Long lecture_id);
}
