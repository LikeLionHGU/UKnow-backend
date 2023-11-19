package hgu.likelion.uknow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lecture {
    @Id
    String code;

    String name;

    Long credit;

    Boolean isEnglish;

    String nonMajor;

    public static Lecture toAdd(String code, String name, Long credit, Boolean isEnglish, String nonMajor) {
        return Lecture.builder()
                .code(code)
                .name(name)
                .credit(credit)
                .isEnglish(isEnglish)
                .nonMajor(nonMajor)
                .build();
    }
}
