package hgu.likelion.uknow.userlecture.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.presentation.response.UserResponse;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLectureResponse {

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
}