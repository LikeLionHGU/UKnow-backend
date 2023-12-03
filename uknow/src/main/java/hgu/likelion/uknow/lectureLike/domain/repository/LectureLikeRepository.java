package hgu.likelion.uknow.lectureLike.domain.repository;


import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lectureLike.domain.entity.LectureLike;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureLikeRepository extends JpaRepository<LectureLike, Long>{

    List<LectureLike> findByUserIdAndLectureId(User user_studentId, Lecture lecture_id);
    //void  deleteByLikerIdAndAnswerId(Member liker_id, Answer answer_id);

    List<LectureLike> findByUserId(User user_studentId);
}
