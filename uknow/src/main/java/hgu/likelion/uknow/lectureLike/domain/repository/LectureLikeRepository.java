package hgu.likelion.uknow.lectureLike.domain.repository;


import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lectureLike.domain.entity.LectureLike;
import hgu.likelion.uknow.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureLikeRepository extends JpaRepository<LectureLike, Long>{

    List<LectureLike> findByUserIdAndLectureId(User user_studentId, Lecture lecture_id);
    //void  deleteByLikerIdAndAnswerId(Member liker_id, Answer answer_id);
}
