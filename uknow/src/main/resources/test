const axios = require("axios");
const express = require("express");
const cors = require("cors");
const cheerio = require("cheerio");

const qs = require("qs");
const iconv = require("iconv-lite");

const app = express();

app.use(cors());
app.use(express.json()); // JSON 파싱 미들웨어

/**
 * @brief 학생 로그인 API
 */

app.post("/login", async (req, res) => {
  const { id, password } = req.body;
  try {
    const sessionCookie = await hisnetLoginAndFetchData({ id, password });
    res.json({ success: true, sessionCookie });
  } catch (error) {
    res.json({ success: false, message: error.message });
  }
});

const PORT = 5050;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});

//////////////////////////////////////////////////// 백엔드 구성

const HISNET_LOGIN_URL = "https://hisnet.handong.edu/login/_login.php";
const HISNET_GRADUATE_URL =
  "https://hisnet.handong.edu/prof/graduate/PGRA123S_gong.php?gubun=hak";

/**
 * @brief 학생 로그인 후 졸업심사 데이터를 가져오는 함수
 */
const hisnetLoginAndFetchData = async ({ id, password }) => {
  try {
    // 세션
    let userSession;

    // 로그인 요청 파라미터
    const params = {
      id: id,
      password: password,
      Language: "Korean",
    };

    // 로그인 POST 요청 전송
    const doc = await axios
      .post(HISNET_LOGIN_URL, qs.stringify(params), {
        headers: { "content-type": "application/x-www-form-urlencoded" },
        responseType: "arraybuffer",
      })
      .then(async (res) => {
        userSession = res.headers["set-cookie"][0].split(";")[0].split("=")[1];
        console.log(userSession);

        const content = iconv.decode(res.data, "EUC-KR"); // 디코딩

        if (content.includes("로그인 된 후, 원하는 페이지로 이동")) {
          await getUserGradInfo(userSession);
          console.log("[👏 졸업 조건 불러오기 성공]\nGood:)");
        }
      });
    return userSession;
  } catch (err) {
    console.log("[🚨 졸업 조건 불러오기 실패]\n\n" + err);
    throw err;
  }
};

/**
 * @brief 졸업 심사 데이터를 가져오는 함수
 */
const getUserGradInfo = (session) =>
  axios
    .get(HISNET_GRADUATE_URL, {
      responseType: "arraybuffer",
      headers: {
        Accept:
          "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        "Accept-Encoding": "gzip, deflate, br",
        "Accept-Language": "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
        Connection: "keep-alive",
        Cookie: `PHPSESSID=${session}`,
        Host: "hisnet.handong.edu",
        Referer: "https://hisnet.handong.edu/haksa/graduate/HGRA120M.php",
        "Sec-Ch-Ua":
          '"Google Chrome";v="117", "Not;A=Brand";v="8", "Chromium";v="117"',
        "Sec-Ch-Ua-Mobile": "?0",
        "Sec-Ch-Ua-Platform": '"macOS"',
        "Sec-Fetch-Dest": "frame",
        "Sec-Fetch-Mode": "navigate",
        "Sec-Fetch-Site": "same-origin",
        "Sec-Fetch-User": "?1",
        "Upgrade-Insecure-Requests": "1",
        "User-Agent":
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
      },
    })
    .then((response) => {
      // console.log(iconv.decode(response.data, "euc-kr"));

      //파싱 데이터 출력
      parseGradeInfoFromHtml(iconv.decode(response.data, "euc-kr"));
    })
    .catch((error) => {
      console.error("eeeeeeError fetching data:", error);
    });

const parseGradeInfoFromHtml = (html) => {
  const $ = cheerio.load(html);

  const result = [];

  $("table#att_list").each((_, table) => {
    const currentTable = [];
    $(table)
      .find("tbody tr")
      .each((_, row) => {
        const rowData = $(row)
          .find("td")
          .map((_, cell) => $(cell).text().trim())
          .get();
        currentTable.push(rowData);
      });
    result.push(currentTable);
  });

  console.log(result);
  excelExport(result);

  // console.log(tables); // ['데이터1', '데이터2']
};

const excelExport = (result) => {
  const ExcelJS = require("exceljs");

  const workbook = new ExcelJS.Workbook();

  // result 배열을 이용해서 각 테이블을 별도의 시트로 저장합니다.
  result.forEach((tableData, index) => {
    const worksheet = workbook.addWorksheet(`Table ${index + 1}`);

    // Add rows using both the direct array approach and by providing an object when the column
    // has keys. Note: the row object can also define styles.
    tableData.forEach((row) => {
      worksheet.addRow(row);
    });
  });

  // 엑셀 파일을 저장합니다.
  workbook.xlsx.writeFile("tables.xlsx").then(() => {
    console.log("File saved!");
  });
};
