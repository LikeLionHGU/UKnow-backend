package hgu.likelion.uknow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberLecture> memberLectureList;


    public static Member toAdd(String name, String major, String semester, String studentId) {
        return Member.builder()
                .name(name)
                .major(major)
                .semester(semester)
                .studentId(studentId)
                .build();
    }
}
