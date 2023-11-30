package hgu.likelion.uknow.userPlan.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanLectureResponse {

    private Long id;
    private LectureResponse lectureResponse;
    private LectureType lectureType;

    public static PlanLectureResponse toResponse(PlanLecture planLecture) {
        return PlanLectureResponse.builder()
                .id(planLecture.getId())
                .lectureResponse(LectureResponse.toResponse(planLecture.getLecture()))
                .lectureType(planLecture.getLectureType())
                .build();
    }

}
