package hgu.likelion.uknow.userPlan.domain.entity;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanLecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PlanTable planTable;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lecture;

    private LectureType lectureType;

    public static PlanLecture toPlanLecture(PlanTable planTable, Lecture lecture, LectureType lectureType) {



        return PlanLecture.builder()
                .planTable(planTable)
                .lecture(lecture)
                .lectureType(lectureType)  //어떻게 넣어야 되는지 모르겠넹
                .build();


    }

}
