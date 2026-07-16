package maddog.sqlexceltoyml;

import lombok.extern.slf4j.Slf4j;
import maddog.sqlexceltoyml.converter.ExcelToYmlConverter;
import maddog.sqlexceltoyml.util.Util;
import maddog.sqlexceltoyml.validator.YmlValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * 主程式
 * 用於 informix to oracle 資料驗證 SQL
 */
@Slf4j
public class Application {

    public static void main(String[] args) {
        Path sourceDirPath;
        Path resultDir;
        Path exceptionDir;

        // 取得 excel to yml 轉換器
        ExcelToYmlConverter excelToYmlConverter = new ExcelToYmlConverter();
        // 取得 yml 驗證器
        YmlValidator ymlValidator = new YmlValidator();

        String appPath = System.getProperty("jpackage.app-path");
        // 取得 excel 檔所在資料夾
        sourceDirPath = appPath != null ?
                Path.of(appPath).getParent().getParent() // exe -> 專案資料夾 -> xlsx 資料夾 (exe 啟動 正式)
                : Path.of("").toAbsolutePath(); // 啟動資料夾 (IDE 啟動 開發測試)
        log.info("抓取 excel 的資料夾路徑為: {}", sourceDirPath);

        try {
            // 取得 yml 輸出資料夾
            resultDir = Files.createDirectories(sourceDirPath.resolve("result"));

            // 取得 txt 輸出資料夾
            exceptionDir = Files.createDirectories(sourceDirPath.resolve("exception"));
        } catch (IOException e) {
            log.error("創建 result 或 exception 資料夾錯誤: ", e);

            // 結束程式
            return;
        }

        try (Stream<Path> sourcePathsStream = Files.list(sourceDirPath)) {
            // 取得該資料夾內所有目標檔案 (非 ~$ 開頭的 .xlsx 檔)
            List<Path> sourcePathList = sourcePathsStream
                    .filter(file -> {
                        String fileName = file.getFileName().toString();

                        return fileName.endsWith(".xlsx") && !fileName.startsWith("~$");
                    })
                    .toList();
            log.info("找到 {} 個 excel 檔", sourcePathList.size());

            // 將每個檔案進行轉換且驗證
            for (Path source : sourcePathList) {
                // 讀取 excel 轉換成 yml
                Path resultPath = excelToYmlConverter.transform(source, resultDir, exceptionDir);

                if (resultPath != null) {
                    // 驗證 yml 檔是否合法
                    ymlValidator.validate(resultPath);
                } else {
                    log.warn("{} transform 失敗回傳 null ，跳過驗證步驟！", Util.getBaseName(source.getFileName().toString()));
                }
            }
        } catch (Exception e) {
            log.error("發生錯誤: ", e);
        }
    }

}
