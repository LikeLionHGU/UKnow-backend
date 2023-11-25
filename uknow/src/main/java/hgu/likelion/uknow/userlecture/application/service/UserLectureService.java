package hgu.likelion.uknow.userlecture.application.service;

import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.domain.repository.UserLectureRepository;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLectureService {
    private final UserLectureRepository userLectureRepository;

    @Transactional
    public List<UserLectureResponse> getLectureListByStudentId(String studentId) {
        List<UserLecture> userLectureList = userLectureRepository.findByStudentId(studentId);
        List<UserLectureResponse> userLectureResponseList = userLectureList.stream().map(UserLectureResponse::toResponse).collect(Collectors.toList());

        return userLectureResponseList;
    }
}
