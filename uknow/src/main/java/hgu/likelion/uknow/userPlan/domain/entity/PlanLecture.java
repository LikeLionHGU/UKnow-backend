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

}
