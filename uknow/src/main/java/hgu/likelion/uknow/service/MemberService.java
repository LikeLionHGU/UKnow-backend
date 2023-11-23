package hgu.likelion.uknow.service;


import hgu.likelion.uknow.entity.Lecture;
import hgu.likelion.uknow.entity.Member;
import hgu.likelion.uknow.entity.MemberLecture;
import hgu.likelion.uknow.repository.LectureRepository;
import hgu.likelion.uknow.repository.MemberLectureRepository;
import hgu.likelion.uknow.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;
    private final MemberLectureRepository memberLectureRepository;

    @Transactional
    public boolean isSignUp(String studentId) {
        boolean isSignUp = memberRepository.existsById(studentId);

        return isSignUp;
    }

    @Transactional
    public String addUser(List<List<List<String>>> userInfoList) {
        String name = userInfoList.get(0).get(0).get(3);
        String studentId = userInfoList.get(0).get(1).get(1);
        String semester = userInfoList.get(0).get(2).get(1);
        String major = userInfoList.get(0).get(3).get(1);

        memberRepository.save(Member.toAdd(name, major, semester, studentId));

        return null;
    }


    @Transactional
    public void addUserLectureList(List<List<List<String>>> userInfoList) {
        String studentId = userInfoList.get(0).get(1).get(1);
        boolean christianWorldView = false;
        Member member = memberRepository.findById(studentId).orElse(null);

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
                                    MemberLecture memberLecture = MemberLecture.toAdd(year, semester, member, newLecture);
                                    memberLectureRepository.save(memberLecture);
                                    christianWorldView = true;
                                }
                            } else {
                                MemberLecture memberLecture = MemberLecture.toAdd(year, semester, member, newLecture);
                                memberLectureRepository.save(memberLecture);
                            }
                        } else {

                            if(lecture.get(0).getName().equals("기독교 세계관 (Towards a Christian Worldview)")) {
                                if(christianWorldView == false) {
                                    MemberLecture memberLecture = MemberLecture.toAdd(year, semester, member, lecture.get(0));
                                    memberLectureRepository.save(memberLecture);
                                    christianWorldView = true;
                                }
                            } else {
                                MemberLecture memberLecture = MemberLecture.toAdd(year, semester, member, lecture.get(0));
                                memberLectureRepository.save(memberLecture);
                            }
                        }

                    }
                }
            }
        }
    }
}
