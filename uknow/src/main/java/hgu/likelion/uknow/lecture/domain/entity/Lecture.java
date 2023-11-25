package hgu.likelion.uknow.lecture.domain.entity;

import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code;

    String name;

    Double credit;

    Boolean isEnglish;

    String nonMajor;

    String type;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL)
    private List<UserLecture> userLectureList;

    public static Lecture toAdd(String code, String name, Double credit, Boolean isEnglish, String nonMajor, String type) {
        return Lecture.builder()
                .code(code)
                .name(name)
                .credit(credit)
                .isEnglish(isEnglish)
                .nonMajor(nonMajor)
                .type(type)
                .build();
    }
}
