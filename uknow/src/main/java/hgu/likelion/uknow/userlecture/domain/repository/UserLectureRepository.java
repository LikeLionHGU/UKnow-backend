package hgu.likelion.uknow.userlecture.domain.repository;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserLectureRepository extends JpaRepository<UserLecture, Long> {

    @Query("select r from UserLecture r where r.user.studentId = :studentId")
    List<UserLecture> findByStudentId(@Param("studentId") String studentId);

    @Query("select r from UserLecture r where r.user.studentId = :studentId and r.lectureType = :type")
    List<UserLecture> findByStudentIdAAndLectureType(@Param("studentId") String studentId, @Param("type")LectureType type);

    @Query("select r from UserLecture r where r.user.studentId = :studentId and r.lecture.id = :lectureId")
    UserLecture findByStudentIdAndLectureId(@Param("studentId") String studentId, @Param("lectureId")Long lectureId);
}
