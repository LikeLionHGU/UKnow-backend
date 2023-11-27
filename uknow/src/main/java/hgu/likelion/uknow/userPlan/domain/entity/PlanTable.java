package hgu.likelion.uknow.userPlan.domain.entity;

import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    String tableName;

    private boolean deleted = Boolean.FALSE;

    public static PlanTable toPlanTable(PlanTableDto dto, User user) {

        return PlanTable.builder()
                .user(user)
                .tableName(dto.getTableName())
                .build();


    }
}
