package hgu.likelion.uknow.userPlan.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeedCreditInfoResponse {
    LectureType lectureType;
    Double credit;
    Double totalCredit;
    Double needCredit;
    Boolean isPassed;

    public static NeedCreditInfoResponse toResponse(UserLectureTotalResponse response) {
        return NeedCreditInfoResponse.builder()
                .lectureType(response.getLectureType())
                .credit(response.getCredit())
                .totalCredit(response.getTotalCredit())
                .needCredit(response.getTotalCredit()-response.getCredit())
                .isPassed(response.getIsPassed())
                .build();
    }
}