package hgu.likelion.uknow.userPlan.domain.repository;

import hgu.likelion.uknow.lectureLike.domain.entity.LectureLike;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanTableRepository extends JpaRepository<PlanTable, Long> {
}
