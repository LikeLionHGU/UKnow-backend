package hgu.likelion.uknow.lectureLike.application.service;


import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.lectureLike.domain.entity.LectureLike;
import hgu.likelion.uknow.lectureLike.domain.repository.LectureLikeRepository;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LectureLikeService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureLikeRepository lectureLikeRepository;

    public Boolean saveLike(Long lecture_id, String studentId, LectureType lectureType) {



        //학번값받아오기
        //String studentId = "22200533";

        User user = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("no such room"));
        Lecture lecture = lectureRepository.findById(Long.toString(lecture_id)).orElseThrow(() -> new IllegalArgumentException("no such room"));

        List<LectureLike> findLectureLike = lectureLikeRepository.findByUserIdAndLectureId(user , lecture);

        //System.out.println(findAnswerLike.isEmpty());
        if (findLectureLike.isEmpty()){

            LectureLike lectureLike = LectureLike.toLectureLike(user, lecture,lectureType);
            lectureLikeRepository.save(lectureLike);
            //br.plusLike(boardId);
            return true;
        }else {
            //System.out.println(findAnswerLike.size());
            //이 답글에 좋아요 누를 사람이 답글의 질문의 질문자인지 확인.

            lectureLikeRepository.deleteById(findLectureLike.get(0).getLecture_like_id());
            //answerLikeRepository.deleteByLikerIdAndAnswerId(member, answer);
            //br.minusLike(boardId);
            return false;

        }

    }

    public boolean isLectureLike(Long lecture_id, String studentId) {

        User user = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("no such room"));
        Lecture lecture = lectureRepository.findById(Long.toString(lecture_id)).orElseThrow(() -> new IllegalArgumentException("no such room"));

        List<LectureLike> findLectureLike = lectureLikeRepository.findByUserIdAndLectureId(user , lecture);


        if (findLectureLike.isEmpty()){
            //유저가 좋아요를 누를 과목이 아니면 false
            return false;
        }else {
            //유저가 좋아요를 누른 과목이면 true
            return true;
        }
    }

}
