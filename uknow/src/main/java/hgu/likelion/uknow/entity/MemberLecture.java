package hgu.likelion.uknow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lecture;

    private String year;

    private String semester;

    public static MemberLecture toAdd(String year, String semester, Member member, Lecture lecture) {
        return MemberLecture.builder()
                .year(year)
                .semester(semester)
                .member(member)
                .lecture(lecture)
                .build();
    }

}
