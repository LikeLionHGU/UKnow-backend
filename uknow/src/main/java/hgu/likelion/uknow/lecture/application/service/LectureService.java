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
            } else if (lectureList.get(i).get(4).equals("1.5")) {
                credit = 1.5;
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

            int index = 0;

            while (lectureResponseList.get(index) != null) {


            }

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                if (userLectureResponseList.get(i).getLectureResponse().getNonMajor().equals("신앙1")) { // 채플에 해당하는 값을 가져오는 리스트
                    chapelList.add(userLectureResponseList.get(i));
                }
            }

            for (int i = 0; i < chapelList.size(); i++) { // 사용자가 들은 chapel list에서 들은 값들을 제외하고 안 들은 값들만 채워서 넣기
                for (int j = 0; j < lectureResponseList.size(); j++) { // 전체 신앙과 관련된 항목에 대해서 추출
                    if (lectureResponseList.get(j).getCode().equals(chapelList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
                    if (lectureResponseList.get(j).getCode().equals(firstFaithList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;

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
                    if (lectureResponseList.get(j).getCode().equals(secondFaithList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
                    if (lectureResponseList.get(j).getCode().equals(firstWorldList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
                    if (lectureResponseList.get(j).getCode().equals(secondWorldList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
                    if (lectureResponseList.get(j).getCode().equals(teamList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
                    if (lectureResponseList.get(j).getCode().equals(serviceList.get(i).getLectureResponse().getCode())) {
                        lectureResponseList.remove(j);
                        j--;
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
            List<Lecture> ECLecture = lectureRepository.findLecturesByCode("GCS10053");
            List<Lecture> ERCLecture = lectureRepository.findLecturesByCode("GCS20003");
            List<Lecture> EAPLecture = lectureRepository.findLecturesByCode("GCS30016"); // 여기서 EAP를 어떻게 가지고 와야하지?
            List<Lecture> EAPLectureSecond = lectureRepository.findLecturesByCode("GCS30017"); // 20-2학기부터 신경써야함
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

            if (GCS10011.size() != 0) {
                returnList.add(GCS10011.get(0));
            }

            if (GEK10077.size() != 0) {
                returnList.add(GEK10077.get(0));
            }

            if (GEK20011.size() != 0) {
                returnList.add(GEK20011.get(0));
            }

            if (GEK20043.size() != 0) {
                returnList.add(GEK20043.get(0));
            }

            if (GEK30030.size() != 0) {
                returnList.add(GEK30030.get(0));
            }

            if (GEK10030.size() != 0) {
                returnList.add(GEK10030.get(0));
            }

            if (GEK10035.size() != 0) {
                returnList.add(GEK10035.get(0));
            }

            if (GEK10040.size() != 0) {
                returnList.add(GEK10040.get(0));
            }

            if (MEC10002.size() != 0) {
                returnList.add(MEC10002.get(0));
            }

            if (MEC10001.size() != 0) {
                returnList.add(MEC10001.get(0));
            }

            if (CSW10003.size() != 0) {
                returnList.add(CSW10003.get(0));
            }

            // return list에 값들이 다 들어가게 됨

            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                        j--;
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

            if (GEK10095.size() != 0) {
                returnList.add(GEK10095.get(0));
            }

            if (GEK10096.size() != 0) {
                returnList.add(GEK10096.get(0));
            }

            if (GEK10097.size() != 0) {
                returnList.add(GEK10097.get(0));
            }

            if (GEK10053.size() != 0) {
                returnList.add(GEK10053.get(0));
            }

            if (GEK10081.size() != 0) {
                returnList.add(GEK10081.get(0));
            }

            if (GEK10082.size() != 0) {
                returnList.add(GEK10082.get(0));
            }

            if (GEK20053.size() != 0) {
                returnList.add(GEK20053.get(0));
            }

            if (ITP20002.size() != 0) {
                returnList.add(ITP20002.get(0));
            }

            if (CCE30023.size() != 0) {
                returnList.add(CCE30023.get(0));
            }

            if (CCE20011.size() != 0) {
                returnList.add(CCE20011.get(0));
            }
            if (GEK10090.size() != 0) {
                returnList.add(GEK10090.get(0));
            }

            if (GEK10055.size() != 0) {
                returnList.add(GEK10055.get(0));
            }

            if (GEK10056.size() != 0) {
                returnList.add(GEK10056.get(0));
            }

            if (GEK10038.size() != 0) {
                returnList.add(GEK10038.get(0));
            }

            if (GEK20038.size() != 0) {
                returnList.add(GEK20038.get(0));
            }

            if (GEK10057.size() != 0) {
                returnList.add(GEK10057.get(0));
            }

            if (GEK10058.size() != 0) {
                returnList.add(GEK10058.get(0));
            }

            if (GEK10094.size() != 0) {
                returnList.add(GEK10094.get(0));
            }

            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                        j--;
                    }
                }
            }

            return returnList;

        } else if (lectureType.equals(LectureType.BSMSet)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.BSM);
            List<LectureResponse> returnList = new ArrayList<>();
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨

            List<LectureResponse> GEK10090 = lectureRepository.findLecturesByCode("GEK10090").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10055 = lectureRepository.findLecturesByCode("GEK10055").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10056 = lectureRepository.findLecturesByCode("GEK10056").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10038 = lectureRepository.findLecturesByCode("GEK10038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK20038 = lectureRepository.findLecturesByCode("GEK20038").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10057 = lectureRepository.findLecturesByCode("GEK10057").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10058 = lectureRepository.findLecturesByCode("GEK10058").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> GEK10094 = lectureRepository.findLecturesByCode("GEK10094").stream().map(LectureResponse::toResponse).collect(Collectors.toList());
            List<LectureResponse> ITP20002 = lectureRepository.findLecturesByCode("ITP20002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 이산 수학

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }

            if (GEK10090.size() != 0) {
                returnList.add(GEK10090.get(0));
            }
            if (ITP20002.size() != 0) {
                returnList.add(ITP20002.get(0));
            }

            if (GEK10055.size() != 0) {
                returnList.add(GEK10055.get(0));
            }

            if (GEK10056.size() != 0) {
                returnList.add(GEK10056.get(0));
            }

            if (GEK10038.size() != 0) {
                returnList.add(GEK10038.get(0));
            }

            if (GEK20038.size() != 0) {
                returnList.add(GEK20038.get(0));
            }

            if (GEK10057.size() != 0) {
                returnList.add(GEK10057.get(0));
            }

            if (GEK10058.size() != 0) {
                returnList.add(GEK10058.get(0));
            }

            if (GEK10094.size() != 0) {
                returnList.add(GEK10094.get(0));
            }

            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                        j--;
                    }
                }
            }

            return returnList;


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
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }


            if (GEK10109.size() != 0) {
                returnList.add(GEK10109.get(0));
            }

            if (GCS10001.size() != 0) {
                returnList.add(GCS10001.get(0));
            }

            if (GCS20010.size() != 0) {
                returnList.add(GCS20010.get(0));
            }

            if (GCS10004.size() != 0) {
                returnList.add(GCS10004.get(0));
            }

            if (GCS10080.size() != 0) {
                returnList.add(GCS10080.get(0));
            }



            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {
                        returnList.remove(j);
                        j--;
                    }
                }
            }

            return returnList;

        } else if (lectureType.equals(LectureType.major)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.major);
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨
            List<LectureResponse> returnList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }


            List<LectureResponse> ECE10002 = lectureRepository.findLecturesByCode("ECE10002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // C 프로그래밍
            if (ECE10002.size() != 0) {
                returnList.add(ECE10002.get(0));
            }
            List<LectureResponse> ITP10003 = lectureRepository.findLecturesByCode("ITP10003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // C 프로그래밍
            if (ITP10003.size() != 0) {
                returnList.add(ITP10003.get(0));
            }
            List<LectureResponse> ECE10005 = lectureRepository.findLecturesByCode("ECE10005").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 코딩 스튜디오
            if (ECE10005.size() != 0) {
                returnList.add(ECE10005.get(0));
            }
            List<LectureResponse> ECE10003 = lectureRepository.findLecturesByCode("ECE10003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // C실습(변경해야함)

            List<LectureResponse> ECE10020 = lectureRepository.findLecturesByCode("ECE10020").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 공학 설계 입문
            if (ECE10020.size() != 0) {
                returnList.add(ECE10020.get(0));
            }
            List<LectureResponse> ECE20010 = lectureRepository.findLecturesByCode("ECE20010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이타구조
            if (ECE20010.size() != 0) {
                returnList.add(ECE20010.get(0));
            }
            List<LectureResponse> ITP20001 = lectureRepository.findLecturesByCode("ITP20001").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이타구조
            if (ITP20001.size() != 0) {
                returnList.add(ITP20001.get(0));
            }
            List<LectureResponse> ECE20016 = lectureRepository.findLecturesByCode("ECE20016").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 자바
            if (ECE20016.size() != 0) {
                returnList.add(ECE20016.get(0));
            }
            List<LectureResponse> ITP20003 = lectureRepository.findLecturesByCode("ITP20003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 자바
            if (ITP20003.size() != 0) {
                returnList.add(ITP20003.get(0));
            }
            List<LectureResponse> ECE20023 = lectureRepository.findLecturesByCode("ECE20023").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // OSS
            if (ECE20023.size() != 0) {
                returnList.add(ECE20023.get(0));
            }
            List<LectureResponse> ITP20004 = lectureRepository.findLecturesByCode("ITP20004").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // OSS
            if (ITP20004.size() != 0) {
                returnList.add(ITP20004.get(0));
            }
            List<LectureResponse> ECE20019 = lectureRepository.findLecturesByCode("ECE20019").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // OSS(변경해야함)

            List<LectureResponse> ECE20057 = lectureRepository.findLecturesByCode("ECE20057").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 논리설계
            if (ECE20057.size() != 0) {
                returnList.add(ECE20057.get(0));
            }
            List<LectureResponse> ITP20007 = lectureRepository.findLecturesByCode("ITP20007").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 논리설계
            if (ITP20007.size() != 0) {
                returnList.add(ITP20007.get(0));
            }
            List<LectureResponse> ECE20064 = lectureRepository.findLecturesByCode("ECE20064").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 회로이론
            if (ECE20064.size() != 0) {
                returnList.add(ECE20064.get(0));
            }
            List<LectureResponse> ECE20051 = lectureRepository.findLecturesByCode("ECE20051").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 회로이론(변경해야함)

            List<LectureResponse> ECE20065 = lectureRepository.findLecturesByCode("ECE20065").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 기초회로 및 논리실습
            if (ECE20065.size() != 0) {
                returnList.add(ECE20065.get(0));
            }
            List<LectureResponse> ECE20006 = lectureRepository.findLecturesByCode("ECE20006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 신호 및 시스템
            if (ECE20006.size() != 0) {
                returnList.add(ECE20006.get(0));
            }
            List<LectureResponse> ECE20008 = lectureRepository.findLecturesByCode("ECE20008").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 실전 프로젝트1
            if (ECE20008.size() != 0) {
                returnList.add(ECE20008.get(0));
            }
            List<LectureResponse> ECE20009 = lectureRepository.findLecturesByCode("ECE20009").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 웹 서비스 개발
            if (ECE20009.size() != 0) {
                returnList.add(ECE20009.get(0));
            }
            List<LectureResponse> ITP20006 = lectureRepository.findLecturesByCode("ITP20006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 웹 서비스 개발
            if (ITP20006.size() != 0) {
                returnList.add(ITP20006.get(0));
            }
            List<LectureResponse> ECE20018 = lectureRepository.findLecturesByCode("ECE20018").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // C++
            if (ECE20018.size() != 0) {
                returnList.add(ECE20018.get(0));
            }
            List<LectureResponse> ECE20021 = lectureRepository.findLecturesByCode("ECE20021").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 구조
            if (ECE20021.size() != 0) {
                returnList.add(ECE20021.get(0));
            }
            List<LectureResponse> ITP30003 = lectureRepository.findLecturesByCode("ITP30003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 구조
            if (ITP30003.size() != 0) {
                returnList.add(ITP30003.get(0));
            }
            List<LectureResponse> ECE20022 = lectureRepository.findLecturesByCode("ECE20022").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 비전
            if (ECE20022.size() != 0) {
                returnList.add(ECE20022.get(0));
            }
            List<LectureResponse> ITP20010 = lectureRepository.findLecturesByCode("ITP20010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 비전
            if (ITP20010.size() != 0) {
                returnList.add(ITP20010.get(0));
            }
            List<LectureResponse> ECE20042 = lectureRepository.findLecturesByCode("ECE20042").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 이산 수학
            if (ECE20042.size() != 0) {
                returnList.add(ECE20042.get(0));
            }
            List<LectureResponse> ITP20002 = lectureRepository.findLecturesByCode("ITP20002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 이산 수학
            if (ITP20002.size() != 0) {
                returnList.add(ITP20002.get(0));
            }
            List<LectureResponse> ECE20063 = lectureRepository.findLecturesByCode("ECE20063").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 디지털 시스템 설계
            if (ECE20063.size() != 0) {
                returnList.add(ECE20063.get(0));
            }
            List<LectureResponse> ECE30002 = lectureRepository.findLecturesByCode("ECE30002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 모바일 앱 개발
            if (ECE30002.size() != 0) {
                returnList.add(ECE30002.get(0));
            }
            List<LectureResponse> ECE30007 = lectureRepository.findLecturesByCode("ECE30007").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // AI 프로젝트 입문
            if (ECE30007.size() != 0) {
                returnList.add(ECE30007.get(0));
            }
            List<LectureResponse> ECE30010 = lectureRepository.findLecturesByCode("ECE30017").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // AI 프로젝트 입문 (변경해야함)
            List<LectureResponse> ECE30011 = lectureRepository.findLecturesByCode("ECE30011").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 알고리즘 분석
            if (ECE30011.size() != 0) {
                returnList.add(ECE30011.get(0));
            }
            List<LectureResponse> ITP30005 = lectureRepository.findLecturesByCode("ITP30005").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 알고리즘 분석
            if (ITP30005.size() != 0) {
                returnList.add(ITP30005.get(0));
            }
            List<LectureResponse> ECE30012 = lectureRepository.findLecturesByCode("ECE30012").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 객체지향 설계패턴
            if (ECE30012.size() != 0) {
                returnList.add(ECE30012.get(0));
            }
            List<LectureResponse> ITP30008 = lectureRepository.findLecturesByCode("ITP30008").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 객체지향 설계패턴
            if (ITP30008.size() != 0) {
                returnList.add(ITP30008.get(0));
            }
            List<LectureResponse> ECE30021 = lectureRepository.findLecturesByCode("ECE30021").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 운영체제
            if (ECE30021.size() != 0) {
                returnList.add(ECE30021.get(0));
            }
            List<LectureResponse> ITP30002 = lectureRepository.findLecturesByCode("ITP30002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 운영체제
            if (ITP30002.size() != 0) {
                returnList.add(ITP30002.get(0));
            }
            List<LectureResponse> ECE30030 = lectureRepository.findLecturesByCode("ECE30030").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터베이스
            if (ECE30030.size() != 0) {
                returnList.add(ECE30030.get(0));
            }
            List<LectureResponse> ITP30010 = lectureRepository.findLecturesByCode("ITP30010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터베이스
            if (ITP30010.size() != 0) {
                returnList.add(ITP30010.get(0));
            }
            List<LectureResponse> ECE30039 = lectureRepository.findLecturesByCode("ECE30039").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 직업과 진로설계
            if (ECE30039.size() != 0) {
                returnList.add(ECE30039.get(0));
            }
            List<LectureResponse> ECE30051 = lectureRepository.findLecturesByCode("ECE30051").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 전자 회로
            if (ECE30051.size() != 0) {
                returnList.add(ECE30051.get(0));
            }
            List<LectureResponse> ECE30075 = lectureRepository.findLecturesByCode("ECE30075").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 임베디드 프로세서 응용
            if (ECE30075.size() != 0) {
                returnList.add(ECE30075.get(0));
            }
            List<LectureResponse> ECE30003 = lectureRepository.findLecturesByCode("ECE30003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // IoT 시스템 설계
            if (ECE30003.size() != 0) {
                returnList.add(ECE30003.get(0));
            }
            List<LectureResponse> ECE30006 = lectureRepository.findLecturesByCode("ECE30006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 프로그래밍 언어론
            if (ECE30006.size() != 0) {
                returnList.add(ECE30006.get(0));
            }
            List<LectureResponse> ITP30011 = lectureRepository.findLecturesByCode("ITP30011").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 프로그래밍 언어론
            if (ITP30011.size() != 0) {
                returnList.add(ITP30011.get(0));
            }
            List<LectureResponse> ECE30018 = lectureRepository.findLecturesByCode("ECE30018").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 문제해결 스튜디오
            if (ECE30018.size() != 0) {
                returnList.add(ECE30018.get(0));
            }
            List<LectureResponse> ECE30017 = lectureRepository.findLecturesByCode("ECE30017").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 문제해결 스튜디오(변경해야함)
            List<LectureResponse> ECE30040 = lectureRepository.findLecturesByCode("ECE30040").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // IT 창업실습
            if (ECE30040.size() != 0) {
                returnList.add(ECE30040.get(0));
            }
            List<LectureResponse> ECE30078 = lectureRepository.findLecturesByCode("ECE30078").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 지능 로봇 제어
            if (ECE30078.size() != 0) {
                returnList.add(ECE30078.get(0));
            }
            List<LectureResponse> ECE30079 = lectureRepository.findLecturesByCode("ECE30079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1
            if (ECE30079.size() != 0) {
                returnList.add(ECE30079.get(0));
            }
            List<LectureResponse> ECE30092 = lectureRepository.findLecturesByCode("ECE30092").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1(변경해야함)

            List<LectureResponse> ECE30086 = lectureRepository.findLecturesByCode("ECE30086").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 네트워크
            if (ECE30086.size() != 0) {
                returnList.add(ECE30086.get(0));
            }
            List<LectureResponse> ECE30087 = lectureRepository.findLecturesByCode("ECE30087").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 확률 변수론
            if (ECE30087.size() != 0) {
                returnList.add(ECE30087.get(0));
            }
            List<LectureResponse> ECE40010 = lectureRepository.findLecturesByCode("ECE40010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 소프트웨어 공학
            if (ECE40010.size() != 0) {
                returnList.add(ECE40010.get(0));
            }
            List<LectureResponse> ITP40002 = lectureRepository.findLecturesByCode("ITP40002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 소프트웨어 공학
            if (ITP40002.size() != 0) {
                returnList.add(ITP40002.get(0));
            }
            List<LectureResponse> ECE40012 = lectureRepository.findLecturesByCode("ECE40012").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴파일러 이론
            if (ECE40012.size() != 0) {
                returnList.add(ECE40012.get(0));
            }
            List<LectureResponse> ITP40004 = lectureRepository.findLecturesByCode("ITP40004").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴파일러 이론
            if (ITP40004.size() != 0) {
                returnList.add(ITP40004.get(0));
            }
            List<LectureResponse> ECE40027 = lectureRepository.findLecturesByCode("ECE40027").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 포스트캡스톤 연구
            if (ECE40027.size() != 0) {
                returnList.add(ECE40027.get(0));
            }
            List<LectureResponse> ECE40035 = lectureRepository.findLecturesByCode("ECE40035").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 딥러닝 개론
            if (ECE40035.size() != 0) {
                returnList.add(ECE40035.get(0));
            }
            List<LectureResponse> ECE40042 = lectureRepository.findLecturesByCode("ECE40042").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 그래픽스
            if (ECE40042.size() != 0) {
                returnList.add(ECE40042.get(0));
            }
            List<LectureResponse> ITP40003 = lectureRepository.findLecturesByCode("ITP40003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 그래픽스
            if (ITP40003.size() != 0) {
                returnList.add(ITP40003.get(0));
            }
            List<LectureResponse> ECE40066 = lectureRepository.findLecturesByCode("ECE40066").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // IoT 실습
            if (ECE40066.size() != 0) {
                returnList.add(ECE40066.get(0));
            }
            List<LectureResponse> ECE40079 = lectureRepository.findLecturesByCode("ECE40079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2
            if (ECE40079.size() != 0) {
                returnList.add(ECE40079.get(0));
            }
            List<LectureResponse> ECE40093 = lectureRepository.findLecturesByCode("ECE40093").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2(변경해야함)

            List<LectureResponse> ECE40097 = lectureRepository.findLecturesByCode("ECE40097").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 특론 1
            if (ECE40097.size() != 0) {
                returnList.add(ECE40097.get(0));
            }
            List<LectureResponse> ECE40007 = lectureRepository.findLecturesByCode("ECE40007").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 다중센서 신호처리
            if (ECE40007.size() != 0) {
                returnList.add(ECE40007.get(0));
            }
            List<LectureResponse> ECE40044 = lectureRepository.findLecturesByCode("ECE40044").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 보안
            if (ECE40044.size() != 0) {
                returnList.add(ECE40044.get(0));
            }
            List<LectureResponse> ECE40049 = lectureRepository.findLecturesByCode("ECE40049").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 딥러닝 영상처리
            if (ECE40049.size() != 0) {
                returnList.add(ECE40049.get(0));
            }
            List<LectureResponse> ECE40052 = lectureRepository.findLecturesByCode("ECE40052").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 집적회로설계
            if (ECE40052.size() != 0) {
                returnList.add(ECE40052.get(0));
            }
            List<LectureResponse> ECE40087 = lectureRepository.findLecturesByCode("ECE40087").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 머신러닝
            if (ECE40087.size() != 0) {
                returnList.add(ECE40087.get(0));
            }
            List<LectureResponse> ITP40010 = lectureRepository.findLecturesByCode("ITP40010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 머신러닝
            if (ITP40010.size() != 0) {
                returnList.add(ITP40010.get(0));
            }


            List<LectureResponse> SIT22009 = lectureRepository.findLecturesByCode("SIT22009").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터과학
            if (SIT22009.size() != 0) {
                returnList.add(SIT22009.get(0));
            }
            List<LectureResponse> SIT32002 = lectureRepository.findLecturesByCode("SIT32002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 인간과 컴퓨터 상호작용
            if (SIT32002.size() != 0) {
                returnList.add(SIT32002.get(0));
            }
            List<LectureResponse> SIT42001 = lectureRepository.findLecturesByCode("SIT42001").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터 시각화
            if (SIT42001.size() != 0) {
                returnList.add(SIT42001.get(0));
            }
            List<LectureResponse> SIT42003 = lectureRepository.findLecturesByCode("SIT42003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 빅 데이터 분석
            if (SIT42003.size() != 0) {
                returnList.add(SIT42003.get(0));
            }
            List<LectureResponse> HMM30100 = lectureRepository.findLecturesByCode("HMM30100").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 디지털 논리회로
            if (HMM30100.size() != 0) {
                returnList.add(HMM30100.get(0));
            }
            List<LectureResponse> HMM20077 = lectureRepository.findLecturesByCode("HMM20077").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 전기 회로
            if (HMM20077.size() != 0) {
                returnList.add(HMM20077.get(0));
            }
            List<LectureResponse> IID30022 = lectureRepository.findLecturesByCode("IID30022").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 스마트 기술 집중 강좌
            if (IID30022.size() != 0) {
                returnList.add(IID30022.get(0));
            }
            List<LectureResponse> HMM30058 = lectureRepository.findLecturesByCode("HMM30058").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 스마트 융합 세미나
            if (HMM30058.size() != 0) {
                returnList.add(HMM30058.get(0));
            }
            List<LectureResponse> HMM10001 = lectureRepository.findLecturesByCode("HMM10001").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 공학 설계 입문
            if (HMM10001.size() != 0) {
                returnList.add(HMM10001.get(0));
            }
            List<LectureResponse> HMM20007 = lectureRepository.findLecturesByCode("HMM20007").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 자동 제어
            if (HMM20007.size() != 0) {
                returnList.add(HMM20007.get(0));
            }
            List<LectureResponse> HMM20006 = lectureRepository.findLecturesByCode("HMM20006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 선형 시스템과 신호
            if (HMM20006.size() != 0) {
                returnList.add(HMM20006.get(0));
            }


            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {

                        if(codes.get(i).equals("ECE10002")) { // C프로그래밍을 수강한 경우
                            returnList.remove(j); // C프로그래밍 현재 분반 삭제
                            j--;

                            for(int k = 0; k < returnList.size(); k++) { // C프로그래밍 다른 분반 삭제
                                if(returnList.get(k).getCode().equals("ITP10003")) {
                                    returnList.remove(k);
                                }
                            }

                        } else if(codes.get(i).equals("ITP10003")) { // C프로그래밍을 수강한 경우
                            returnList.remove(j); // C프로그래밍 현재 분반 삭제
                            j--;

                            for(int k = 0; k < returnList.size(); k++) { // C프로그래밍 다른 분반 삭제
                                if(returnList.get(k).getCode().equals("ECE10002")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE10003")) { // C 실습

                            for(int k = 0; k < returnList.size(); k++) { // 코딩 스튜디오
                                if(returnList.get(k).getCode().equals("ECE10005")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE10020")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("HMM10001")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("HMM10001")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE10020")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20010")) { // 데이터구조

                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20001")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20001")) { // 데이터구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20016")) { // 자바
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20003")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20003")) { // 자바
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20016")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20023")) { // OSS
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20004")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20004")) { // OSS
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20023")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20019")) { // OSS

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20023")) {
                                    returnList.remove(k);

                                } else if(returnList.get(k).getCode().equals("ITP20004")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20057")) { // 논리설계
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20007")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20007")) { // 논리설계
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20057")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20051")) { // 회로이론


                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20064")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20009")) { // 웹 서비스 개발
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20006")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20006")) { // 웹 서비스 개발
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20009")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20021")) { // 컴퓨터 구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30003")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30003")) { // 컴퓨터 구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20021")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20022")) { // 컴퓨터비전
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20010")) { // 컴퓨터비전
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20022")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20042")) { // 이산수학
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20002")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20002")) { // 이산수학
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20042")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30010")) { // AI프로젝트
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30007")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30011")) { // 알고리즘
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30005")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30005")) { // 알고리즘
                            returnList.remove(j);
                            j--;
                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30011")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30012")) { // 객체지향
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30008")) {
                                    returnList.remove(k);

                                }
                            }


                        } else if(codes.get(i).equals("ITP30008")) { // 객체지향
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30012")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ECE30021")) { // 운영체제
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30002")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30002")) { // 운영체제
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30021")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30030")) { // 데이터베이스
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30010")) { // 데이터베이스
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30030")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30006")) { // 프언
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30011")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ITP30011")) { // 프언
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30006")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ECE30017")) { // 문해스


                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30018")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30092")) { // 캡스톤 1

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30079")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40010")) { // 소공
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP40002")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ITP40002")) { // 소공
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40012")) { // 컴파일러
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP40004")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP40004")) { // 컴파일러
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40012")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40042")) { // 그래픽
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP40003")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ITP40003")) { // 그래픽
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40042")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40093")) { // 캡스톤 2

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40079")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40087")) { // 머신러닝
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP40010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP40010")) { // 머신러닝
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40087")) {
                                    returnList.remove(k);

                                }
                            }
                        } else {
                            returnList.remove(j);
                            j--;
                        }

                    }
                }
            }


            return returnList;

        } else if (lectureType.equals(LectureType.majorChoice)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.major);
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨
            List<LectureResponse> returnList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }

            List<LectureResponse> ECE40010 = lectureRepository.findLecturesByCode("ECE40010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 소프트웨어 공학
            if (ECE40010.size() != 0) {
                returnList.add(ECE40010.get(0));
            }
            List<LectureResponse> ITP40002 = lectureRepository.findLecturesByCode("ITP40002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 소프트웨어 공학
            if (ITP40002.size() != 0) {
                returnList.add(ITP40002.get(0));
            }
            List<LectureResponse> ECE30086 = lectureRepository.findLecturesByCode("ECE30086").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 네트워크
            if (ECE30086.size() != 0) {
                returnList.add(ECE30086.get(0));
            }
            List<LectureResponse> ECE30006 = lectureRepository.findLecturesByCode("ECE30006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 프로그래밍 언어론
            if (ECE30006.size() != 0) {
                returnList.add(ECE30006.get(0));
            }
            List<LectureResponse> ITP30011 = lectureRepository.findLecturesByCode("ITP30011").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 프로그래밍 언어론
            if (ITP30011.size() != 0) {
                returnList.add(ITP30011.get(0));
            }
            List<LectureResponse> ECE30011 = lectureRepository.findLecturesByCode("ECE30011").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 알고리즘 분석
            if (ECE30011.size() != 0) {
                returnList.add(ECE30011.get(0));
            }
            List<LectureResponse> ITP30005 = lectureRepository.findLecturesByCode("ITP30005").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 알고리즘 분석
            if (ITP30005.size() != 0) {
                returnList.add(ITP30005.get(0));
            }
            List<LectureResponse> ECE30030 = lectureRepository.findLecturesByCode("ECE30030").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터베이스
            if (ECE30030.size() != 0) {
                returnList.add(ECE30030.get(0));
            }
            List<LectureResponse> ITP30010 = lectureRepository.findLecturesByCode("ITP30010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이터베이스
            if (ITP30010.size() != 0) {
                returnList.add(ITP30010.get(0));
            }

            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {

                        if(codes.get(i).equals("ECE30011")) { // 알고리즘
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30005")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30005")) { // 알고리즘
                            returnList.remove(j);
                            j--;
                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30011")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30030")) { // 데이터베이스
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30010")) { // 데이터베이스
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30030")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30006")) { // 프언
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30011")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ITP30011")) { // 프언
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30006")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ECE40010")) { // 소공
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP40002")) {
                                    returnList.remove(k);

                                }
                            }

                        } else if(codes.get(i).equals("ITP40002")) { // 소공
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else {
                            returnList.remove(j);
                            j--;
                        }

                    }
                }
            }

            return returnList;

        } else if (lectureType.equals(LectureType.majorEssential)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.major);
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨
            List<LectureResponse> returnList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }

            List<LectureResponse> ECE10020 = lectureRepository.findLecturesByCode("ECE10020").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 공학 설계 입문
            if (ECE10020.size() != 0) {
                returnList.add(ECE10020.get(0));
            }

            List<LectureResponse> ECE20010 = lectureRepository.findLecturesByCode("ECE20010").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이타구조
            if (ECE20010.size() != 0) {
                returnList.add(ECE20010.get(0));
            }
            List<LectureResponse> ITP20001 = lectureRepository.findLecturesByCode("ITP20001").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 데이타구조
            if (ITP20001.size() != 0) {
                returnList.add(ITP20001.get(0));
            }

            List<LectureResponse> ECE20021 = lectureRepository.findLecturesByCode("ECE20021").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 구조
            if (ECE20021.size() != 0) {
                returnList.add(ECE20021.get(0));
            }
            List<LectureResponse> ITP30003 = lectureRepository.findLecturesByCode("ITP30003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 컴퓨터 구조
            if (ITP30003.size() != 0) {
                returnList.add(ITP30003.get(0));
            }

            List<LectureResponse> ECE30021 = lectureRepository.findLecturesByCode("ECE30021").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 운영체제
            if (ECE30021.size() != 0) {
                returnList.add(ECE30021.get(0));
            }
            List<LectureResponse> ITP30002 = lectureRepository.findLecturesByCode("ITP30002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 운영체제
            if (ITP30002.size() != 0) {
                returnList.add(ITP30002.get(0));
            }
            List<LectureResponse> ECE30079 = lectureRepository.findLecturesByCode("ECE30079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1
            if (ECE30079.size() != 0) {
                returnList.add(ECE30079.get(0));
            }
            List<LectureResponse> ECE30092 = lectureRepository.findLecturesByCode("ECE30092").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1(변경해야함)

            List<LectureResponse> ECE40079 = lectureRepository.findLecturesByCode("ECE40079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2
            if (ECE40079.size() != 0) {
                returnList.add(ECE40079.get(0));
            }
            List<LectureResponse> ECE40093 = lectureRepository.findLecturesByCode("ECE40093").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2(변경해야함)


            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {

                       if(codes.get(i).equals("ECE10020")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("HMM10001")) {
                                    returnList.remove(k);

                                }
                            }
                       } else if(codes.get(i).equals("ECE20008")) { // 실프

                       } else if(codes.get(i).equals("HMM10001")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE10020")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20010")) { // 데이터구조

                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20001")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20001")) { // 데이터구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20010")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20021")) { // 컴퓨터 구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30003")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30003")) { // 컴퓨터 구조
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20021")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE20042")) { // 이산수학
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20002")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20002")) { // 이산수학
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20042")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30010")) { // AI프로젝트
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30007")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30021")) { // 운영체제
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP30002")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP30002")) { // 운영체제
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30021")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30092")) { // 캡스톤 1

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30079")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40093")) { // 캡스톤 2

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40079")) {
                                    returnList.remove(k);

                                }
                            }
                        }  else {
                            returnList.remove(j);
                            j--;
                        }

                    }
                }
            }
            return returnList;

        } else if (lectureType.equals(LectureType.design)) {
            List<UserLectureResponse> userLectureResponseList = userLectureService.getLectureListByStudentIdAndType(studentId, LectureType.major);
            List<String> codes = new ArrayList<>(); // 사용자가 들은 과목 코드가 여기에 들어가게 됨
            List<LectureResponse> returnList = new ArrayList<>();

            for (int i = 0; i < userLectureResponseList.size(); i++) {
                codes.add(userLectureResponseList.get(i).getLectureResponse().getCode());
            }

            List<LectureResponse> ECE10020 = lectureRepository.findLecturesByCode("ECE10020").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 공학 설계 입문
            if (ECE10020.size() != 0) {
                returnList.add(ECE10020.get(0));
            }
            List<LectureResponse> ECE30079 = lectureRepository.findLecturesByCode("ECE30079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1
            if (ECE30079.size() != 0) {
                returnList.add(ECE30079.get(0));
            }
            List<LectureResponse> ECE30092 = lectureRepository.findLecturesByCode("ECE30092").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 1(변경해야함)

            List<LectureResponse> ECE40079 = lectureRepository.findLecturesByCode("ECE40079").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2
            if (ECE40079.size() != 0) {
                returnList.add(ECE40079.get(0));
            }
            List<LectureResponse> ECE40093 = lectureRepository.findLecturesByCode("ECE40093").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 캡스톤 2(변경해야함)
            List<LectureResponse> ECE30002 = lectureRepository.findLecturesByCode("ECE30002").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 모바일 앱 개발
            if (ECE30002.size() != 0) {
                returnList.add(ECE30002.get(0));
            }
            List<LectureResponse> ECE30007 = lectureRepository.findLecturesByCode("ECE30007").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // AI 프로젝트 입문
            if (ECE30007.size() != 0) {
                returnList.add(ECE30007.get(0));
            }
            List<LectureResponse> ECE30010 = lectureRepository.findLecturesByCode("ECE30017").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // AI 프로젝트 입문 (변경해야함)
            List<LectureResponse> ECE20008 = lectureRepository.findLecturesByCode("ECE20008").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 실전 프로젝트1
            if (ECE20008.size() != 0) {
                returnList.add(ECE20008.get(0));
            }
            List<LectureResponse> ECE20009 = lectureRepository.findLecturesByCode("ECE20009").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 웹 서비스 개발
            if (ECE20009.size() != 0) {
                returnList.add(ECE20009.get(0));
            }
            List<LectureResponse> ITP20006 = lectureRepository.findLecturesByCode("ITP20006").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // 웹 서비스 개발
            if (ITP20006.size() != 0) {
                returnList.add(ITP20006.get(0));
            }
            List<LectureResponse> ECE30003 = lectureRepository.findLecturesByCode("ECE30003").stream().map(LectureResponse::toResponse).collect(Collectors.toList()); // IoT 시스템 설계
            if (ECE30003.size() != 0) {
                returnList.add(ECE30003.get(0));
            }

            for (int i = 0; i < codes.size(); i++) {
                for (int j = 0; j < returnList.size(); j++) {
                    if (codes.get(i).equals(returnList.get(j).getCode())) {

                        if(codes.get(i).equals("ECE10020")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("HMM10001")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("HMM10001")) { // 공설입
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE10020")) {
                                    returnList.remove(k);

                                }
                            }
                        }  else if(codes.get(i).equals("ECE20009")) { // 웹 서비스 개발
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ITP20006")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ITP20006")) { // 웹 서비스 개발
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE20009")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30010")) { // AI프로젝트
                            returnList.remove(j);
                            j--;

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30007")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE30092")) { // 캡스톤 1

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE30079")) {
                                    returnList.remove(k);

                                }
                            }
                        } else if(codes.get(i).equals("ECE40093")) { // 캡스톤 2

                            for(int k = 0; k < returnList.size(); k++) {
                                if(returnList.get(k).getCode().equals("ECE40079")) {
                                    returnList.remove(k);

                                }
                            }
                        } else {
                            returnList.remove(j);
                            j--;
                        }

                    }
                }
            }




            return returnList;
        }


        return null;
    }

}
