package hgu.likelion.uknow.lecture.presentation.response;

import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureResponse {
    Long id;

    String code;

    String name;

    Double credit;

    Boolean isEnglish;

    String nonMajor;

    String type;

    Boolean isLiked;

    public static LectureResponse toResponse(Lecture lecture) {
        return LectureResponse.builder()
                .id(lecture.getId())
                .code(lecture.getCode())
                .name(lecture.getName())
                .credit(lecture.getCredit())
                .isEnglish(lecture.getIsEnglish())
                .nonMajor(lecture.getNonMajor())
                .type(lecture.getType())
                .build();
    }
}
