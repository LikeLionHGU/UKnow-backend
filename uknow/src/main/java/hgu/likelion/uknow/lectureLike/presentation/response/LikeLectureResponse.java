package hgu.likelion.uknow.lectureLike.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.lectureLike.domain.entity.LectureLike;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeLectureResponse {

    private LectureResponse lectureResponse;
    private LectureType lectureType;

    public static LikeLectureResponse toResponse(LectureLike lecturelike) {
        System.out.println("=====>4");
        return LikeLectureResponse.builder()
                .lectureResponse(LectureResponse.toResponse(lecturelike.getLectureId()))
                .lectureType(lecturelike.getLectureType())
                .build();
    }

}
