package hgu.likelion.uknow.userPlan.application.dto;

import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.userPlan.presentation.request.PlanTableRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanTableDto {

    Long id;

    private User user;

    String tableName;


    private boolean deleted = Boolean.FALSE;

    public static PlanTableDto toAddScrapFolder(PlanTableRequest request) {
        return PlanTableDto.builder()
                .tableName(request.getTableName())
                .build();
    }
}
