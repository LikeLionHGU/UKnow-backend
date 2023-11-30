package hgu.likelion.uknow.userPlan.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPlanResponse {
    LectureType lectureType;
    Double credit;
    Double totalCredit;
    Double needCredit;
    Boolean isPassed;

    public static UserPlanResponse toResponse(UserLectureTotalResponse userLecture) {
        return UserPlanResponse.builder()
                .lectureType(userLecture.getLectureType())
                .credit(userLecture.getCredit())
                .totalCredit(userLecture.getTotalCredit())
                .needCredit(userLecture.getTotalCredit() - userLecture.getCredit())
                .isPassed(userLecture.getIsPassed())
                .build();
    }
}