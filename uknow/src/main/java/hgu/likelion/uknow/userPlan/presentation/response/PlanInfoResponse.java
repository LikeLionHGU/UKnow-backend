package hgu.likelion.uknow.userPlan.presentation.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanInfoResponse {
    Long tableId;
    String tableName;
    List<PlanLectureResponse> userPlanLectureResponse;

}