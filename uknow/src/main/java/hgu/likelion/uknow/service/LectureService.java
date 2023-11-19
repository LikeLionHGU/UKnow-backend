package hgu.likelion.uknow.service;

import hgu.likelion.uknow.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public void addLecture() {

    }
}
