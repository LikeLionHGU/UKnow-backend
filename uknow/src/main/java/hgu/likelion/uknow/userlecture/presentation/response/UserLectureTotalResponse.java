package hgu.likelion.uknow.userlecture.presentation.response;

import hgu.likelion.uknow.common.LectureType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLectureTotalResponse {
    List<UserLectureResponse> userLectureResponseList;
    LectureType lectureType;
    Double credit;
    Double totalCredit;
    Boolean isPassed;
}