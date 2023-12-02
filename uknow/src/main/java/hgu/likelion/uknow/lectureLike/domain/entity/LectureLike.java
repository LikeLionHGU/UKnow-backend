package hgu.likelion.uknow.lectureLike.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@Where(clause = "deleted = false")
//@SQLDelete(sql = "UPDATE answer_like SET deleted = true WHERE answer_like_id = ?")
public class LectureLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lecture_like_id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User userId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lectureId;

    LectureType lectureType;

    private boolean deleted = Boolean.FALSE;

    public static LectureLike toLectureLike(User userId, Lecture lectureId, LectureType lectureType) {



        return LectureLike.builder()
                .userId(userId)
                .lectureId(lectureId)
                .lectureType(lectureType)
                .build();


    }
}
