package hgu.likelion.uknow.userlecture.application.service;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.domain.repository.UserLectureRepository;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public List<UserLectureResponse> getLectureListByStudentIdAndType(String studentId, LectureType lectureType) {
        List<UserLecture> userLectureList = userLectureRepository.findByStudentIdAAndLectureType(studentId, lectureType);
        List<UserLectureResponse> userLectureResponseList = userLectureList.stream().map(UserLectureResponse::toResponse).collect(Collectors.toList());

        return userLectureResponseList;
    }

    @Transactional
    public List<UserLectureTotalResponse> checkStudentLecture(String studentId) {
        List<UserLectureTotalResponse> userLectureTotalResponseList = new ArrayList<>();

        // 신앙 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음
        List<UserLectureResponse> userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.faith);
        double credit = 0;
        double firstFaith = 0;
        double secondFaith = 0;
        double firstWorld = 0;
        double secondWorld = 0;
        boolean isPassed = false;

        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초1")) {
                firstFaith += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초2")) {
                secondFaith += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관1")) {
                firstWorld += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관2")) {
                secondWorld += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }
        }

        if(firstFaith >= 2 && secondFaith >= 2 && firstWorld >= 2 && secondWorld >= 3) {
            isPassed = true;
        }

        UserLectureTotalResponse userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.faith)
                .totalCredit(9.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        // 인성 및 리더십 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.leaderShip);

        credit = 0;
        double leader = 0; // 공동체 리더십 훈련
        double service = 0; // 사회 봉사
        double edu = 0; // 한동 인성 교육
        isPassed = false;
        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();
            // 팀모임 1부터 8까지에 해당
            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10008") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10009") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20008") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20009") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30008") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30009") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK40008") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10009")) {
                leader += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10046") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20046") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20047") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30047")) {
                service += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10015")) {
                edu += userLectureResponseList.get(i).getLectureResponse().getCredit();
            }
        }

        if(leader >= 3.0 && service >= 2.0 && edu >= 1.0) {
            isPassed = true;
        }


        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.leaderShip)
                .totalCredit(6.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);


        // 실무 영어 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.english);

        credit = 0;
        Boolean EAP = false;
        isPassed = false;
        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();
            // 팀모임 1부터 8까지에 해당
            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30016") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30017") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30008") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30009") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30010") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30011") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30012") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30013") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30014") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30015")) {
                EAP = true;
            }
        }

        if(credit >= 9.0 || EAP) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.english)
                .totalCredit(9.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);


        // 전문 교양 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.professionalCulture);

        credit = 0;
        isPassed = false;
        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();
        }

        if(credit >= 5.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.professionalCulture)
                .totalCredit(5.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);


        // BSM 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.BSM);

        credit = 0;
        isPassed = false;
        Boolean eduPhysics = false; // 물리학개론
        Boolean firstEduPhysics = false; // 물리학 1
        Boolean secondEduPhysics = false; // 물리학 2
        Boolean firstExpPhysics = false; // 물리학실험 1
        Boolean secondExpPhysics = false; // 물리학실험 2
        Boolean generalChemical = false; // 일반 화학
        Boolean generalExpChemical = false; // 일반 화학 실험

        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10090")) { // 물리학개론
                eduPhysics = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10038")) { // 물리학실험 1
                firstExpPhysics = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20038")) { // 물리학실험 2
                secondExpPhysics = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10055")) { // 물리학 1
                firstEduPhysics = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10056")) { // 물리학 2
                secondEduPhysics = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10058")) { // 일반화학
                generalChemical = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10094")) { // 일반화학실험
                generalChemical = true;
            }
        }

        if(credit >= 18.0 && ((eduPhysics && firstExpPhysics) || (eduPhysics && secondExpPhysics) || (firstEduPhysics && firstExpPhysics)
        || (secondEduPhysics && secondExpPhysics) || (generalChemical && generalExpChemical))) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.BSM)
                .totalCredit(18.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);



        // ICT 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.ICT);

        credit = 0;
        isPassed = false;
        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();
        }

        if(credit >= 2.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.ICT)
                .totalCredit(2.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);


        // 교양 선택 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.culture);

        credit = 0;
        isPassed = false;
        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();
        }

        if(credit >= 9.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.culture)
                .totalCredit(9.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);



        // 전공 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.major);

        credit = 0;
        isPassed = false;
        Boolean ED = false; // 공학 설계 입문
        Boolean DS = false; // 데이터 구조
        Boolean CA = false; // 컴퓨터 구조
        Boolean OS = false; // 운영체제
        Boolean firstCapstone = false; // 공프기 / 캡스톤 1
        Boolean secondCapstone = false; // 캡스톤 2
        Boolean PP = false; // 실프
        Boolean AI = false; // AI프로젝트입문
        int count = 0;

        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE10020")) { // 공학설계입문
                ED = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20010") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP20001")) { // DS
                DS = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20021") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30003")) { // CA
                CA = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30092") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30021")) { // OS
                OS = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30079") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30092")) { // 캡스톤 1
                firstCapstone = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE40079")) { // 캡스톤 2
                secondCapstone = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20008")) { // 실프
                PP = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30007")) { // AI
                AI = true;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30011") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP40002") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30011") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30005") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30086") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30030") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30010")) { // AI ITP30010
                count++;
            }
        }

        if((credit >= 60.0) && PP && AI && firstCapstone && secondCapstone && ED && DS && CA && OS && (count >= 2)) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.major)
                .totalCredit(6.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        return userLectureTotalResponseList;
    }
}
