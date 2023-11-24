package hgu.likelion.uknow.user.domain.entity;

import hgu.likelion.uknow.common.Authority;
import hgu.likelion.uknow.common.BaseEntity;
import hgu.likelion.uknow.userlecture.UserLecture;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    private String studentId;

    private String name;

    private String major;

    private String semester;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLecture> userLectureList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();

    public void setRoles(List<Authority> roles) {
        this.roles = roles;
        roles.forEach(o -> o.setUser(this));
    }


    public static User toAdd(String name, String major, String semester, String studentId) {
        return User.builder()
                .name(name)
                .major(major)
                .semester(semester)
                .studentId(studentId)
                .build();
    }
}
