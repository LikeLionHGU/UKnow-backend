package hgu.likelion.uknow.userlecture.application.service;

import hgu.likelion.uknow.userlecture.domain.repository.UserLectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLectureService {
    private final UserLectureRepository userLectureRepository;
}
