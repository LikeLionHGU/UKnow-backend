package hgu.likelion.uknow.lecture.application.service;

import hgu.likelion.uknow.common.LectureType;
import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import hgu.likelion.uknow.lecture.presentation.response.LectureResponse;
import hgu.likelion.uknow.userlecture.application.service.UserLectureService;
import hgu.likelion.uknow.userlecture.presentation.response.UserLectureResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserLectureService userLectureService;

    @Transactional
    public void addLecture(List<List<String>> lectureList) {

        for (int i = 1; i < lectureList.size(); i++) {
            String type = lectureList.get(i).get(0);
            String code = lectureList.get(i).get(1);
            String name = lectureList.get(i).get(3);

            Double credit;

            if (lectureList.get(i).get(4).equals(".5")) {
                credit = 0.5;
            } else {
                credit = Double.valueOf(Long.valueOf(lectureList.get(i).get(4)));
            }

            Boolean isEnglish;
            if (lectureList.get(i).get(10).equals("100%")) {
                isEnglish = true;
            } else {
                isEnglish = false;
            }
            String nonMajor = lectureList.get(i).get(11);

            Lecture lecture = lectureRepository.findByCodeAndEnglish(code, isEnglish);

            if (lecture == null) {
                Lecture newLecture = Lecture.toAdd(code, name, credit, isEnglish, nonMajor, type);
                lectureRepository.save(newLecture);
            }
        }
    }

    @Transactional
    public List<List<String>> parseLecture(String html) {
        Document document = Jsoup.parse(html);
        List<List<String>> currentTable = new ArrayList<>();

        Elements tables = document.select("table");

        for (Element table : tables) {
            Elements rows = table.select("tr");


            for (Element row : rows) {
                List<String> rowData = new ArrayList<>();
                Elements cols = row.select("td");

                for (Element col : cols) {
                    rowData.add(col.text().trim());
                }
                currentTable.add(rowData);
            }

        }

        return currentTable;
    }

    @Transactional
    public List<LectureResponse> getLectureByName(String name) {
        List<Lecture> lectureList = lectureRepository.findLecturesByNameContains(name);
        List<LectureResponse> lectureResponseList = lectureList.stream().map(LectureResponse::toResponse).collect(Collectors.toList());

        return lectureResponseList;
    }

    @Transactional
    public List<LectureResponse> haveToTake(LectureType lectureType, String studentId) {
        // input으로 들어온 LectureType에 따라서 값을 다르게 사용

        if (lectureType.equals(LectureType.chapel)) {
            // faith로 분류된 값들 중에서 신앙에 관련된 값들만 분류해서 가지고 와야함
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.faith);
            List<UserLectureResponse> chapelList = new ArrayList<>();
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNonMajorEquals("신앙1").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            // lectureResponseList에서 신앙1과 관련된 모든 lecture에 대한 정보를 가지고 옴

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("신앙1")) { // 채플에 해당하는 값을 가져오는 리스트
                    chapelList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < chapelList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(chapelList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                        // 전체 리스트에서 사용자가 들은 과목들은 제거해주는 과정
                    }
                }
            }
            return lectureResponseList;

        } else if (lectureType.equals(LectureType.firstFaith)) {

            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.faith);
            List<UserLectureResponse> firstFaithList = new ArrayList<>();
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNonMajorEquals("기독교신앙기초1").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            // lectureResponseList에서 신앙1과 관련된 모든 lecture에 대한 정보를 가지고 옴

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초1")) { // 채플에 해당하는 값을 가져오는 리스트
                    firstFaithList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < firstFaithList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(firstFaithList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                        // 전체 리스트에서 사용자가 들은 과목들은 제거해주는 과정
                    }
                }
            }
            return lectureResponseList;

        } else if (lectureType.equals(LectureType.secondFaith)) {

            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.faith);
            List<UserLectureResponse> secondFaithList = new ArrayList<>();
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNonMajorEquals("기독교신앙기초2").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            // lectureResponseList에서 신앙1과 관련된 모든 lecture에 대한 정보를 가지고 옴

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("기독교신앙기초2")) { // 채플에 해당하는 값을 가져오는 리스트
                    secondFaithList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < secondFaithList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(secondFaithList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                        // 전체 리스트에서 사용자가 들은 과목들은 제거해주는 과정
                    }
                }
            }
            return lectureResponseList;

        } else if (lectureType.equals(LectureType.firstWorld)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.faith);
            List<UserLectureResponse> firstWorldList = new ArrayList<>();
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNonMajorEquals("세계관1").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            // lectureResponseList에서 신앙1과 관련된 모든 lecture에 대한 정보를 가지고 옴

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관1")) { // 채플에 해당하는 값을 가져오는 리스트
                    firstWorldList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < firstWorldList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(firstWorldList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                        // 전체 리스트에서 사용자가 들은 과목들은 제거해주는 과정
                    }
                }
            }
            return lectureResponseList;

        } else if (lectureType.equals(LectureType.secondWorld)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.faith);
            List<UserLectureResponse> secondWorldList = new ArrayList<>();
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNonMajorEquals("세계관2").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            // lectureResponseList에서 신앙1과 관련된 모든 lecture에 대한 정보를 가지고 옴

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("세계관2")) { // 채플에 해당하는 값을 가져오는 리스트
                    secondWorldList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < secondWorldList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(secondWorldList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                        // 전체 리스트에서 사용자가 들은 과목들은 제거해주는 과정
                    }
                }
            }
            return lectureResponseList;


        } else if (lectureType.equals(LectureType.team)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.leaderShip);
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNameContains("공동체리더십훈련").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<UserLectureResponse> teamList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10008") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10009") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20008") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20009") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30008") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30009") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK40008") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10009")) {

                    teamList.add(userLectureResponseList.get(i));
                }
            }


            for (int i = 0; i < teamList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(teamList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                    }
                }
            }

            return lectureResponseList;

        } else if (lectureType.equals(LectureType.service)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.leaderShip);
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNameContains("사회봉사").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<UserLectureResponse> serviceList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10046") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20046") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK20047") ||
                        userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK30047")) {

                    serviceList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < serviceList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getName().equals(serviceList.get(i).getLectureResponse().getName())) {
                        lectureResponseList.remove(j);
                    }
                }
            }

            return lectureResponseList;

        } else if (lectureType == LectureType.edu) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.leaderShip);
            List<LectureResponse> lectureResponseList = lectureRepository.findLecturesByNameContains("한동인성교육").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<UserLectureResponse> eduList = new ArrayList<>();


            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GEK10015")) {

                    eduList.add(userLectureResponseList.get(i));
                }
            }

            if (eduList.size() == 1) {
                return null;
            } else {
                return lectureResponseList;
            }

        } else if (lectureType.equals(LectureType.english)) {
            Boolean EF = false;
            Boolean EC = false;
            Boolean ERC = false;
            Boolean EAP = false;
            List<Lecture> EFLecture = lectureRepository.findLecturesByCode("GCS10052");
            List<Lecture> ECLecture = lectureRepository.findLecturesByCode("GCS10052");
            List<Lecture> ERCLecture = lectureRepository.findLecturesByCode("GCS10052");
            List<Lecture> EAPLecture = lectureRepository.findLecturesByCode("GCS30014");
            List<Lecture> EAPLectureSecond = lectureRepository.findLecturesByCode("GCS30015");
            List<LectureResponse> returnList = new ArrayList<>();

            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.english);

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS10052")) {
                    EF = true;
                } else if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS10053")) {
                    EC = true;
                } else if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS20003")) {
                    ERC = true;
                } else if (userLectureResponseList.get(i).getLectureResponse().getCode().equals("GCS30016") ||
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

            if (EF == false) {
                returnList.add(LectureResponse.toResponse(EFLecture.get(0)));
            }

            if (EC == false) {
                returnList.add(LectureResponse.toResponse(ECLecture.get(0)));
            }

            if (ERC == false) {
                returnList.add(LectureResponse.toResponse(ERCLecture.get(0)));
            }

            if (EAP == false) {
                returnList.add(LectureResponse.toResponse(EAPLecture.get(0)));
                returnList.add(LectureResponse.toResponse(EAPLectureSecond.get(0)));
            }

            return returnList;

        } else if (lectureType.equals(LectureType.professionalCulture)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.professionalCulture);
            List<LectureResponse> returnList = new ArrayList<>();

            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨

            List<LectureResponse> GCS10011 = lectureRepository.findLecturesByCode("GCS10011").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10077 = lectureRepository.findLecturesByCode("GEK10077").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20011 = lectureRepository.findLecturesByCode("GEK20011").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20043 = lectureRepository.findLecturesByCode("GEK20043").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK30030 = lectureRepository.findLecturesByCode("GEK30030").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10030 = lectureRepository.findLecturesByCode("GEK10030").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10035 = lectureRepository.findLecturesByCode("GEK10035").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10040 = lectureRepository.findLecturesByCode("GEK10040").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> MEC10002 = lectureRepository.findLecturesByCode("MEC10002").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> MEC10001 = lectureRepository.findLecturesByCode("MEC10001").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> CSW10003 = lectureRepository.findLecturesByCode("CSW10003").stream().map(LectureResponse::toResponse).collect(Collectors.toList());

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }

            if (GCS10011 != null) {
                returnList.add(GCS10011.get(0));
            }

            if (GEK10077 != null) {
                returnList.add(GEK10077.get(0));
            }

            if(GEK20011 != null) {
                returnList.add(GEK20011.get(0));
            }

            if(GEK20043 != null) {
                returnList.add(GEK20043.get(0));
            }

            if(GEK30030 != null) {
                returnList.add(GEK30030.get(0));
            }

            if(GEK10030 != null) {
                returnList.add(GEK10030.get(0));
            }

            if(GEK10035 != null) {
                returnList.add(GEK10035.get(0));
            }

            if(GEK10040 != null) {
                returnList.add(GEK10040.get(0));
            }

            if(MEC10002 != null) {
                returnList.add(MEC10002.get(0));
            }

            if(MEC10001 != null) {
                returnList.add(MEC10001.get(0));
            }

            if(CSW10003 != null) {
                returnList.add(CSW10003.get(0));
            }

            // return list에 값들이 다 들어가게 됨

            for(int i = 0; i < codes.size(); i++) {
                for(int j = 0; j < returnList.size(); j++) {
                    if(codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                    }
                }
            }

            return returnList;

            // 이공계 글쓰기 GCS10011
            // 창의적 문제해결 GEK10077
            // GEK20011 기독교 세계
            // 공학윤리 GEK20043
            // 현대과학 GEK30030
            // 철학 개 GEK10030
            // 한국사 GEK10035
            // 사회학개 GEK10040
            // 경영학 입문 MEC10002
            // 경제학 입문 MEC10001
            // CSW10003 심리학개론


        } else if (lectureType.equals(LectureType.BSM)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.BSM);
            List<LectureResponse> returnList = new ArrayList<>();
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨



            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }


            List<LectureResponse> GEK10095 = lectureRepository.findLecturesByCode("GEK10095").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10096 = lectureRepository.findLecturesByCode("GEK10096").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10097 = lectureRepository.findLecturesByCode("GEK10097").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10053 = lectureRepository.findLecturesByCode("GEK10053").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10081 = lectureRepository.findLecturesByCode("GEK10081").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10082 = lectureRepository.findLecturesByCode("GEK10082").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20053 = lectureRepository.findLecturesByCode("GEK20053").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> ITP20002 = lectureRepository.findLecturesByCode("ITP20002").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> CCE30023 = lectureRepository.findLecturesByCode("CCE30023").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> CCE20011 = lectureRepository.findLecturesByCode("CCE20011").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10090 = lectureRepository.findLecturesByCode("GEK10090").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10055 = lectureRepository.findLecturesByCode("GEK10055").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10056 = lectureRepository.findLecturesByCode("GEK10056").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10038 = lectureRepository.findLecturesByCode("GEK10038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20038 = lectureRepository.findLecturesByCode("GEK20038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10057 = lectureRepository.findLecturesByCode("GEK10057").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10058 = lectureRepository.findLecturesByCode("GEK10058").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10094 = lectureRepository.findLecturesByCode("GEK10094").stream().map(LectureResponse::toResponse).collect(Collectors.toList());

            if(GEK10095 != null) {
                returnList.add(GEK10095.get(0));
            }

            if(GEK10096 != null) {
                returnList.add(GEK10096.get(0));
            }

            if(GEK10097 != null) {
                returnList.add(GEK10097.get(0));
            }

            if(GEK10053 != null) {
                returnList.add(GEK10053.get(0));
            }

            if(GEK10081 != null) {
                System.out.println("!@#!@#!@#!@#!@#!@");
                System.out.println(GEK10081.size());
                System.out.println(GEK10081.get(0).getName());
                returnList.add(GEK10081.get(0));
            }

            if(GEK10082 != null) {
                returnList.add(GEK10082.get(0));
            }

            if(GEK20053 != null) {
                returnList.add(GEK20053.get(0));
            }

            if(ITP20002 != null) {
                returnList.add(ITP20002.get(0));
            }

            if(CCE30023 != null) {
                returnList.add(CCE30023.get(0));
            }

            if(CCE20011 != null) {
                returnList.add(CCE20011.get(0));
            }
            if(GEK10090 != null) {
                returnList.add(GEK10090.get(0));
            }

            if(GEK10055 != null) {
                returnList.add(GEK10055.get(0));
            }

            if(GEK10056 != null) {
                returnList.add(GEK10056.get(0));
            }

            if(GEK10038 != null) {
                returnList.add(GEK10038.get(0));
            }

            if(GEK20038 != null) {
                returnList.add(GEK20038.get(0));
            }

            if(GEK10057 != null) {
                returnList.add(GEK10057.get(0));
            }

            if(GEK10058 != null) {
                returnList.add(GEK10058.get(0));
            }

            if(GEK10094 != null) {
                returnList.add(GEK10094.get(0));
            }

            for(int i = 0; i < codes.size(); i++) {
                for(int j = 0; j < returnList.size(); j++) {
                    if(codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                    }
                }
            }

            return returnList;

        } else if (lectureType.equals(LectureType.BSMSet)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.BSM);
            List<LectureResponse> returnList = new ArrayList<>();

            List<LectureResponse> GEK10090 = lectureRepository.findLecturesByCode("GEK10090").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10055 = lectureRepository.findLecturesByCode("GEK10055").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10056 = lectureRepository.findLecturesByCode("GEK10056").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10038 = lectureRepository.findLecturesByCode("GEK10038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20038 = lectureRepository.findLecturesByCode("GEK20038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10057 = lectureRepository.findLecturesByCode("GEK10057").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10058 = lectureRepository.findLecturesByCode("GEK10058").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10094 = lectureRepository.findLecturesByCode("GEK10094").stream().map(LectureResponse::toResponse).collect(Collectors.toList());

            if(GEK10090 != null) {
                returnList.add(GEK10090.get(0));
            }

            if(GEK10055 != null) {
                returnList.add(GEK10055.get(0));
            }

            if(GEK10056 != null) {
                returnList.add(GEK10056.get(0));
            }

            if(GEK10038 != null) {
                returnList.add(GEK10038.get(0));
            }

            if(GEK20038 != null) {
                returnList.add(GEK20038.get(0));
            }

            if(GEK10057 != null) {
                returnList.add(GEK10057.get(0));
            }

            if(GEK10058 != null) {
                returnList.add(GEK10058.get(0));
            }

            if(GEK10094 != null) {
                returnList.add(GEK10094.get(0));
            }

            for(int i = 0; i < userLectureResponseList.size(); i++) {
                for(int j = 0; j < returnList.size(); j++) {
                    if(userLectureResponseList.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                    }
                }
            }



        } else if (lectureType.equals(LectureType.ICT)) {
            // GEK10109 모두를 위한
            // GCS10001 소입
            // GCS20010 데이터 수집과 응용
            // GCS10004 파이썬
            // GCS10080 R을 이용한 빅 데이터 분석
            List<LectureResponse> GEK10109 = lectureRepository.findLecturesByCode("GEK10109").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GCS10001 = lectureRepository.findLecturesByCode("GCS10001").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GCS20010 = lectureRepository.findLecturesByCode("GCS20010").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GCS10004 = lectureRepository.findLecturesByCode("GCS10004").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GCS10080 = lectureRepository.findLecturesByCode("GCS10080").stream().map(LectureResponse::toResponse).collect(Collectors.toList());

            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.ICT);
            List<LectureResponse> returnList = new ArrayList<>();

            if(userLectureResponseList == null) {
                if(GEK10109 != null) {
                    returnList.add(GEK10109.get(0));
                }

                if(GCS10001 != null) {
                    returnList.add(GCS10001.get(0));
                }

                if(GCS20010 != null) {
                    returnList.add(GCS20010.get(0));
                }

                if(GCS10004 != null) {
                    returnList.add(GCS10004.get(0));
                }

                if(GCS10080 != null) {
                    returnList.add(GCS10080.get(0));
                }
            }

            return returnList;

        } else if (lectureType.equals(LectureType.major)) {

        } else if (lectureType.equals(LectureType.majorChoice)) {

        } else if (lectureType.equals(LectureType.majorEssential)) {

        } else if(lectureType.equals(LectureType.design)) {

        }


        return null;
    }

}
