package hgu.likelion.uknow.userlecture.presentation.controller;

import hgu.likelion.uknow.userlecture.application.service.UserLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserLectureController {
    private final UserLectureService userLectureService;
}
