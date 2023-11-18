package hgu.likelion.uknow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id
    private String studentId;

    private String name;

    private String major;

    private String semester;

    public static Member toAdd(String name, String major, String semester, String studentId) {
        return Member.builder()
                .name(name)
                .major(major)
                .semester(semester)
                .studentId(studentId)
                .build();
    }
}
