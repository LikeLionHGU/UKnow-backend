package hgu.likelion.uknow.userlecture.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.presentation.response.UserResponse;
import hgu.likelion.uknow.userPlan.presentation.response.PlanLectureResponse;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLectureResponse { // 전체 리스트를 그냥 return 해줌

    Long id;

    private LectureResponse lectureResponse;

    private String year;

    private String semester;

    private LectureType lectureType;

    public static UserLectureResponse toResponse(UserLecture userLecture) {
        return UserLectureResponse.builder()
                .id(userLecture.getId())
                .lectureResponse(LectureResponse.toResponse(userLecture.getLecture()))
                .year(userLecture.getYear())
                .semester(userLecture.getSemester())
                .lectureType(userLecture.getLectureType())
                .build();
    }

    public static UserLectureResponse toPlanLectureResponse(PlanLectureResponse userLecture) {
        return UserLectureResponse.builder()
                .id(null)
                .lectureResponse(userLecture.getLectureResponse())
                .year(null)
                .semester(null)
                .lectureType(userLecture.getLectureType())
                .build();
    }


}
