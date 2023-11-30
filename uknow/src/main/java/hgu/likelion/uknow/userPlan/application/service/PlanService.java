package hgu.likelion.uknow.userPlan.application.service;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.lectureLike.domain.repository.LectureLikeRepository;
import hgu.likelion.uknow.user.domain.entity.User;
import hgu.likelion.uknow.user.domain.repository.UserRepository;
import hgu.likelion.uknow.userPlan.application.dto.PlanTableDto;
import hgu.likelion.uknow.userPlan.domain.entity.PlanLecture;
import hgu.likelion.uknow.userPlan.domain.entity.PlanTable;
import hgu.likelion.uknow.userPlan.domain.repository.PlanLectureRepository;
import hgu.likelion.uknow.userPlan.domain.repository.PlanTableRepository;
import hgu.likelion.uknow.userPlan.presentation.response.NeedCreditInfoResponse;
import hgu.likelion.uknow.userPlan.presentation.response.PlanInfoResponse;
import hgu.likelion.uknow.userPlan.presentation.response.PlanLectureResponse;
import hgu.likelion.uknow.userlecture.application.service.UserLectureService;
import hgu.likelion.uknow.userlecture.domain.entity.UserLecture;
import hgu.likelion.uknow.userlecture.domain.repository.UserLectureRepository;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureTotalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureLikeRepository lectureLikeRepository;
    private final PlanTableRepository planTableRepository;
    private final PlanLectureRepository planLectureRepository;
    private final UserLectureRepository userLectureRepository;

    @Transactional
    public Long addPlanTable(PlanTableDto dto, String studentId) {

        //Category category = categoryRepository.findById(dto.getCategory_id()).orElseThrow(() -> new IllegalArgumentException("no such Category"));
        //Question question = questionRepository.findById(question_id).orElseThrow(() -> new IllegalArgumentException("no such room"));
        User user = userRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("no such room"));

        PlanTable newPlanTable = planTableRepository.save(PlanTable.toPlanTable(dto, user));

        return newPlanTable.getId();
    }

    @Transactional
    public Boolean addPlanLecture(Long planTable_id, Long lecture_id) {
        System.out.println("===>1");
        PlanTable planTable = planTableRepository.findById(planTable_id).orElseThrow(() -> new IllegalArgumentException("no such scrapFolder"));
        System.out.println("===>2");
        Lecture lecture = lectureRepository.findById(Long.toString(lecture_id)).orElseThrow(() -> new IllegalArgumentException("no such question"));


        String code = lecture.getCode();

        List<PlanLecture> findPlanLecture = planLectureRepository.findByPlanTableIdAndLectureId(planTable.getId(), lecture.getId());
        System.out.println(planTable.getTableName() + " + " + lecture.getName());


        if (findPlanLecture.isEmpty()) {
            // 중복이 아니라면
            PlanLecture setPlanLecture = PlanLecture.toPlanLecture(planTable, lecture);
            planLectureRepository.save(setPlanLecture);

            // 위에 정보 업데이트를 통해서 각 분류에서 몇학점 남았는지 다시 계산해서 리스폰스로 리턴 해야함.

            return true;
        } else {
            // 중복이라면 백에서 처리 해주나요? 프론트에서 처리해주나욤

            return false;

        }

    }
    @Transactional
    public List<PlanInfoResponse> planInfo(String studentId){

        List<PlanInfoResponse> PlanInfoResponseList = new ArrayList<>();
        List<PlanTable> userPlanTablelist = planTableRepository.findByStudentId(studentId);

        for(PlanTable t : userPlanTablelist){

            PlanInfoResponse planInfoResponse = new PlanInfoResponse();


            List<PlanLecture> findPlanLecturelistInTable = planLectureRepository.findByPlanTableId(t.getId());
            List<PlanLectureResponse> planLecturelist = findPlanLecturelistInTable.stream().map(PlanLectureResponse::toResponse).collect(Collectors.toList());

            planInfoResponse.setTableId(t.getId());
            planInfoResponse.setTableName(t.getTableName());
            planInfoResponse.setUserPlanLectureResponse(planLecturelist);
            PlanInfoResponseList.add(planInfoResponse);
        }


        return  PlanInfoResponseList;
    }

    @Transactional
    public List<NeedCreditInfoResponse> needCreditInfo(String studentId){

        List<PlanLectureResponse> planLecturelist = new ArrayList<>();

        //유저에 맞는 모든 테이블을 불러옴.
        List<PlanTable> userPlanTablelist = planTableRepository.findByStudentId(studentId);


        //각 테이블에 있는 모든 플랜과목 정보를 불러옴.
        for(PlanTable t : userPlanTablelist){

            List<PlanLecture> findPlanLecturelistInTable = planLectureRepository.findByPlanTableId(t.getId());
            planLecturelist.addAll(findPlanLecturelistInTable.stream().map(PlanLectureResponse::toResponse).collect(Collectors.toList()));

        }

        System.out.println(planLecturelist.size());

        //유저랙처 리스폰에 유저렉쳐와, 계획렉쳐를 다 넣음.
        //다 넣은 유저랙쳐들로 토탈을 계산함.
        List<UserLectureTotalResponse> userLectureTotalResponse = checkStudentLecture(studentId, planLecturelist);


        //필요한정보만 골라서 List<NeedCreditInfoResponse>
        List<NeedCreditInfoResponse> needCreditInfoResponseList = userLectureTotalResponse.stream().map(NeedCreditInfoResponse::toResponse).collect(Collectors.toList());

        return needCreditInfoResponseList;

    }

    @Transactional
    public List<UserLectureResponse> getLectureListByStudentIdAndType(String studentId, LectureType lectureType) {
        List<UserLecture> userLectureList = userLectureRepository.findByStudentIdAAndLectureType(studentId, lectureType);
        List<UserLectureResponse> userLectureResponseList = userLectureList.stream().map(UserLectureResponse::toResponse).collect(Collectors.toList());

        return userLectureResponseList;
    }

    @Transactional
    public List<UserLectureTotalResponse> checkStudentLecture(String studentId, List<PlanLectureResponse> planLectureResponselist) {
        List<UserLectureTotalResponse> userLectureTotalResponseList = new ArrayList<>();

        // 신앙 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음
        List<UserLectureResponse> userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.faith);

        List<UserLectureResponse> chapelList = new ArrayList<>();
        List<UserLectureResponse> firstFaithList = new ArrayList<>();
        List<UserLectureResponse> seconFaithList = new ArrayList<>();
        List<UserLectureResponse> firstWorldList = new ArrayList<>();
        List<UserLectureResponse> secondWorldList = new ArrayList<>();
        double credit = 0;
        int chapelCount = 0;
        double firstFaith = 0;
        double secondFaith = 0;
        double firstWorld = 0;
        double secondWorld = 0;
        boolean isPassed = false;

        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초1")) {
                firstFaith += userLectureResponseList.get(i).getLectureResponse().getCredit();
                firstFaithList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초2")) {
                secondFaith += userLectureResponseList.get(i).getLectureResponse().getCredit();
                secondWorldList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관1")) {
                firstWorld += userLectureResponseList.get(i).getLectureResponse().getCredit();
                firstWorldList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관2")) {
                secondWorld += userLectureResponseList.get(i).getLectureResponse().getCredit();
                secondWorldList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("신앙1")) {
                chapelCount += 1;
                chapelList.add(userLectureResponseList.get(i));
            }

        }

        if(chapelCount >= 6) {
            isPassed = true;
        }
        UserLectureTotalResponse userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(chapelList)
                .lectureType(LectureType.chapel)
                .totalCredit(0.0)
                .credit((double) chapelCount)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(firstFaith >= 2) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(firstFaithList)
                .lectureType(LectureType.firstFaith)
                .totalCredit(2.0)
                .credit(firstFaith)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(secondFaith >= 2) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(seconFaithList)
                .lectureType(LectureType.secondFaith)
                .totalCredit(2.0)
                .credit(secondFaith)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(firstWorld >= 2) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(firstWorldList)
                .lectureType(LectureType.firstWorld)
                .totalCredit(2.0)
                .credit(firstWorld)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(secondWorld >= 3) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(secondWorldList)
                .lectureType(LectureType.secondWorld)
                .totalCredit(3.0)
                .credit(secondWorld)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        // 인성 및 리더십 부분에 대해서 현재 졸업 심사 결과가 합격 되었는지 여부를 확인할 수 있음

        userLectureResponseList = getLectureListByStudentIdAndType(studentId, LectureType.leaderShip);

        credit = 0;
        double leader = 0; // 공동체 리더십 훈련
        double service = 0; // 사회 봉사
        double edu = 0; // 한동 인성 교육

        List<UserLectureResponse> teamList = new ArrayList<>();
        List<UserLectureResponse> serviceList = new ArrayList<>();
        List<UserLectureResponse> eduList = new ArrayList<>();

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
                teamList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10046") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20046") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20047") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30047")) {
                service += userLectureResponseList.get(i).getLectureResponse().getCredit();
                serviceList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10015")) {
                edu += userLectureResponseList.get(i).getLectureResponse().getCredit();
                eduList.add(userLectureResponseList.get(i));
            }
        }

        if(leader >= 3.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(teamList)
                .lectureType(LectureType.team)
                .totalCredit(3.0)
                .credit(leader)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(service >= 2.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(serviceList)
                .lectureType(LectureType.service)
                .totalCredit(2.0)
                .credit(service)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(edu >= 2.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(eduList)
                .lectureType(LectureType.edu)
                .totalCredit(1.0)
                .credit(edu)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;


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
        Double setCredit = 0.0;
        Boolean eduPhysics = false; // 물리학개론
        Boolean firstEduPhysics = false; // 물리학 1
        Boolean secondEduPhysics = false; // 물리학 2
        Boolean firstExpPhysics = false; // 물리학실험 1
        Boolean secondExpPhysics = false; // 물리학실험 2
        Boolean generalChemical = false; // 일반 화학
        Boolean generalExpChemical = false; // 일반 화학 실험
        List<UserLectureResponse> setList = new ArrayList<>();

        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10090")) { // 물리학개론
                eduPhysics = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 3.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10038")) { // 물리학실험 1
                firstExpPhysics = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20038")) { // 물리학실험 2
                secondExpPhysics = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10055")) { // 물리학 1
                firstEduPhysics = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 3.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10056")) { // 물리학 2
                secondEduPhysics = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 3.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10058")) { // 일반화학
                generalChemical = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 3.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10094")) { // 일반화학실험
                generalChemical = true;
                setList.add(userLectureResponseList.get(i));
                setCredit += 1.0;
            }
        }

        if(credit >= 18.0) {
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

        isPassed = false;

        if(((eduPhysics && firstExpPhysics) || (eduPhysics && secondExpPhysics) || (firstEduPhysics && firstExpPhysics)
                || (secondEduPhysics && secondExpPhysics) || (generalChemical && generalExpChemical))) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(setList)
                .lectureType(LectureType.BSMSet)
                .totalCredit(4.0)
                .credit(setCredit)
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
        // 설계
        // 공설입 3 실프 1 웹서개 1 디자인시스템설계 1 모앱개 1 AI프로젝트입문 1 객체지향설계패턴 1 임베디드 1 IOT 1 캡스톤디자인1 2 캡스톤디자인2 4

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
        Double designCredit = 0.0; // 설계 학점
        Double majorEssentialCredit = 0.0;
        Double majorChoiceCredit = 0.0; // 선택과목 2과목 들었는지 여부 확인하는 부분
        List<UserLectureResponse> designList = new ArrayList<>(); // 설계 학점 리스트
        List<UserLectureResponse> majorChoiceList = new ArrayList<>(); // 전공 선택 리스트
        List<UserLectureResponse> majorEssentialList = new ArrayList<>(); // 전공 필수 리스트


        for(int i = 0; i < userLectureResponseList.size(); i++) {
            credit += userLectureResponseList.get(i).getLectureResponse().getCredit();

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE10020")) { // 공학설계입문
                ED = true;
                designList.add(userLectureResponseList.get(i));
                designCredit += 3.0;
                majorEssentialCredit += 3.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20010") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP20001")) { // DS
                DS = true;
                majorEssentialCredit += 3.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20021") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30003")) { // CA
                CA = true;
                majorEssentialCredit += 3.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30092") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30021")) { // OS
                OS = true;
                majorEssentialCredit += 3.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30079") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30092")) { // 캡스톤 1
                firstCapstone = true;
                designList.add(userLectureResponseList.get(i));
                designCredit += 2.0;
                majorEssentialCredit += 2.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE40079")) { // 캡스톤 2
                secondCapstone = true;
                majorEssentialCredit += 4.0;
                designList.add(userLectureResponseList.get(i));
                designCredit += 4.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20008")) { // 실프
                PP = true;
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
                majorEssentialCredit += 3.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30007")) { // AI
                AI = true;
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
                majorEssentialCredit += 2.0;
                majorEssentialList.add(userLectureResponseList.get(i));
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20009") || // 웹 서비스 개발
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP20006")) {
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE20063")) { // 디지털시스템 설계
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30002")) { // 모앱개
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30012") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30008")) { // 객체지향 설계패턴
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30075")) { // 임베디드 ECE30003
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30003")) { // IOT
                designList.add(userLectureResponseList.get(i));
                designCredit += 1.0;
            }

            if(userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30011") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP40002") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30011") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30005") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30086") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ECE30030") ||
                    userLectureResponseList.get(i).getLectureResponse().getCode().equals("ITP30010")) { // 전공 선택에 대한 부분
                majorChoiceList.add(userLectureResponseList.get(i));
                majorChoiceCredit += 3.0;
            }
        }

        if(credit >= 60.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(userLectureResponseList)
                .lectureType(LectureType.major)
                .totalCredit(60.0)
                .credit(credit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(PP && AI && firstCapstone && secondCapstone && ED && DS && CA && OS) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(majorEssentialList)
                .lectureType(LectureType.majorEssential)
                .totalCredit(23.0)
                .credit(majorEssentialCredit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(majorChoiceCredit >= 6.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(majorChoiceList)
                .lectureType(LectureType.majorChoice)
                .totalCredit(6.0)
                .credit(majorEssentialCredit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        if(designCredit >= 12.0) {
            isPassed = true;
        }

        userLectureTotalResponse = UserLectureTotalResponse.builder()
                .userLectureResponseList(designList)
                .lectureType(LectureType.design)
                .totalCredit(6.0)
                .credit(designCredit)
                .isPassed(isPassed)
                .build();
        userLectureTotalResponseList.add(userLectureTotalResponse);

        isPassed = false;

        return userLectureTotalResponseList;
    }

}