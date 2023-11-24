package hgu.likelion.uknow.user.application.service;


import hgu.likelion.uknow.common.Authority;
import hgu.likelion.uknow.dto.request.HisnetRequest;
import hgu.likelion.uknow.jwt.JwtProvider;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.presentation.response.UserResponse;
import hgu.likelion.uknow.userlecture.UserLecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.userlecture.UserLectureRepository;
import hgu.likelion.uknow.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final UserLectureRepository userLectureRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public boolean isSignUp(String studentId) {
        boolean isSignUp = userRepository.existsById(studentId);

        return isSignUp;
    }

    @Transactional
    public User addUser(List<List<List<String>>> userInfoList) {
        String name = userInfoList.get(0).get(0).get(3);
        String studentId = userInfoList.get(0).get(1).get(1);
        String semester = userInfoList.get(0).get(2).get(1);
        String major = userInfoList.get(0).get(3).get(1);
        User user = User.toAdd(name, major, semester, studentId);
        user.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

        userRepository.save(user);

        return user;
    }

    @Transactional
    public UserResponse login(List<List<List<String>>> userInfoList, String session) {
        String studentId = userInfoList.get(0).get(1).get(1);
        String name = userInfoList.get(0).get(0).get(3);

        User user = userRepository.findById(studentId).orElse(null);

        return UserResponse.toResponse(studentId, session, name, jwtProvider.createToken(user.getStudentId(), user.getRoles()));
    }


    @Transactional
    public void addUserLectureList(List<List<List<String>>> userInfoList) {
        String studentId = userInfoList.get(0).get(1).get(1);
        boolean christianWorldView = false;
        User user = userRepository.findById(studentId).orElse(null);

        for(int i = 0; i < userInfoList.size(); i++) { // index 0 : user information 1 : total credit

            if(i >= 2) {
                List<List<String>> temp = userInfoList.get(i);

                for(int j = 0; j < temp.size(); j++) {

                    if(j != 0 && j != temp.size() - 1) {
                        List<String> lectureString = temp.get(j);

                        String year = lectureString.get(1);
                        String semester = lectureString.get(2);
                        String code = lectureString.get(3);

                        List<Lecture> lecture = lectureRepository.findByCode(code); // code가 같지만 영어가 true / false로 나뉠 수 있음

                        if(lecture.size() == 0) { // 해당 lecture가 없을 경우에 추가 해줘야 함
                            Double credit = Double.valueOf(lectureString.get(5));
                            String name = lectureString.get(4);
                            String type = lectureString.get(0);

                            Lecture newLecture = Lecture.toAdd(code, name, credit, null, null, type);
                            lectureRepository.save(newLecture);
                            if(newLecture.getName().equals("기독교 세계관 (Towards a Christian Worldview)")) {
                                if(christianWorldView == false) {
                                    UserLecture userLecture = UserLecture.toAdd(year, semester, user, newLecture);
                                    userLectureRepository.save(userLecture);
                                    christianWorldView = true;
                                }
                            } else {
                                UserLecture userLecture = UserLecture.toAdd(year, semester, user, newLecture);
                                userLectureRepository.save(userLecture);
                            }
                        } else {

                            if(lecture.get(0).getName().equals("기독교 세계관 (Towards a Christian Worldview)")) {
                                if(christianWorldView == false) {
                                    UserLecture userLecture = UserLecture.toAdd(year, semester, user, lecture.get(0));
                                    userLectureRepository.save(userLecture);
                                    christianWorldView = true;
                                }
                            } else {
                                UserLecture userLecture = UserLecture.toAdd(year, semester, user, lecture.get(0));
                                userLectureRepository.save(userLecture);
                            }
                        }

                    }
                }
            }
        }
    }
}
