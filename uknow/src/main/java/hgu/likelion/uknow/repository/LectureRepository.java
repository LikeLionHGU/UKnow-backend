package hgu.likelion.uknow.repository;

import hgu.likelion.uknow.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, String> {
    @Query("select r from Lecture r where r.code = :code and r.isEnglish =:isEnglish")
    Lecture findByCodeAndEnglish(@Param("code") String code, @Param("isEnglish") Boolean isEnglish);

    @Query("select r from Lecture r where r.code = :code")
    List<Lecture> findByCode(@Param("code") String code);
}
