package hgu.likelion.uknow.lecture.application.service;

import hgu.likelion.uknow.lecture.domain.entity.Lecture;
import hgu.likelion.uknow.lecture.domain.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public void addLecture(List<List<String>> lectureList) {

        for(int i = 1; i < lectureList.size(); i++) {
            String type = lectureList.get(i).get(0);
            String code = lectureList.get(i).get(1);
            String name = lectureList.get(i).get(3);

            Double credit;

            if(lectureList.get(i).get(4).equals(".5")) {
                credit = 0.5;
            } else {
                credit = Double.valueOf(Long.valueOf(lectureList.get(i).get(4)));
            }

            Boolean isEnglish;
            if(lectureList.get(i).get(10).equals("100%")) {
                isEnglish = true;
            } else {
                isEnglish = false;
            }
            String nonMajor = lectureList.get(i).get(11);

            Lecture lecture = lectureRepository.findByCodeAndEnglish(code, isEnglish);

            if(lecture == null) {
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

        for(Element table : tables) {
            Elements rows = table.select("tr");


            for(Element row : rows) {
                List<String> rowData = new ArrayList<>();
                Elements cols = row.select("td");

                for(Element col : cols) {
                    rowData.add(col.text().trim());
                }
                currentTable.add(rowData);
            }

        }

        return currentTable;
    }
}
