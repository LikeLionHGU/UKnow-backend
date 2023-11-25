package hgu.likelion.uknow.userlecture.domain.repository;

import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLectureRepository extends JpaRepository<UserLecture, Long> {
}
