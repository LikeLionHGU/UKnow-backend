package hgu.likelion.uknow.userlecture;

import hgu.likelion.uknow.userlecture.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLectureRepository extends JpaRepository<UserLecture, Long> {
}
