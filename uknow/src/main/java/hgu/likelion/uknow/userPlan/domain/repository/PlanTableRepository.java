package hgu.likelion.uknow.userPlan.domain.repository;

import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlanTableRepository extends JpaRepository<PlanTable, Long> {

    @Query("select r from PlanTable r where r.user.studentId = :studentId")
    List<PlanTable> findByStudentId(@Param("studentId") String studentId);
}
