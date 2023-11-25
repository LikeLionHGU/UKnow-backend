package hgu.likelion.uknow.userlecture.domain.entity;

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
public class UserLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lecture;

    private String year;

    private String semester;

    private LectureType lectureType;

    public static UserLecture toAdd(String year, String semester, User user, Lecture lecture, LectureType lectureType) {
        return UserLecture.builder()
                .year(year)
                .semester(semester)
                .user(user)
                .lecture(lecture)
                .lectureType(lectureType)
                .build();
    }

}
