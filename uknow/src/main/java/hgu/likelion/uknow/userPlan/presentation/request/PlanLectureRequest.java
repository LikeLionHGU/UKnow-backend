package hgu.likelion.uknow.userPlan.presentation.request;

import hgu.likelion.uknow.common.LectureType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanLectureRequest {

    LectureType lectureType;

}
