package hgu.likelion.uknow.repository;

import hgu.likelion.uknow.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, String> {
}
