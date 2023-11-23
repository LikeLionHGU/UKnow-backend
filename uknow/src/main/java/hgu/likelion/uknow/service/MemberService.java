package hgu.likelion.uknow.service;


import hgu.likelion.uknow.entity.Member;
import hgu.likelion.uknow.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

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
        for(int i = 0; i < userInfoList.size(); i++) { // index 0 : user information 1 : total credit

            if(i >= 2) {
                List<List<String>> temp = userInfoList.get(i);

                for(int j = 0; j < temp.size(); j++) {

                    if(j != 0) {

                    }
                }
            }
        }
    }
}
