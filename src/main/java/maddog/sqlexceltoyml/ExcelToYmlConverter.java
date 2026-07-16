package maddog.sqlexceltoyml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * excel to yml 轉換主程式
 * 用於 informix to oracle 資料驗證 SQL
 */
@Slf4j
public class ExcelToYmlConverter {

    public static void main(String[] args) {
        Path sourceDirPath;
        Path resultDir;
        Path exceptionDir;

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
                Path resultPath = transform(source, resultDir, exceptionDir);

                if (resultPath != null) {
                    // 驗證 yml 檔是否合法
                    verifyYml(resultPath);
                } else {
                    log.warn("{} transform 失敗回傳 null ，跳過驗證步驟！", getBaseName(source.getFileName().toString()));
                }
            }
        } catch (Exception e) {
            log.error("發生錯誤: ", e);
        }

    }

    /**
     * 核心方法，讀取 excel 轉換成 yml
     *
     * @param source
     *          excel 路徑
     * @param resultDir
     *          yml 輸出資料夾 路徑
     * @param exceptionDir
     *          txt 輸出資料夾 路徑
     * @return resultPath
     *          yml 檔路徑
     */
    private static Path transform(Path source,
                                  Path resultDir,
                                  Path exceptionDir) {
        // yml 檔路徑
        Path resultPath = null;
        // txt 檔路徑
        Path exceptionPath;

        // 系統名
        String systemName = DEFAULT_SYSTEM_NAME;

        // 創建 StringBuilder 儲存結果
        StringBuilder result = new StringBuilder();
        result.append(YML_HEADER);

        // 創建 StringBuilder 儲存結果記錄
        StringBuilder resultLog = new StringBuilder();

        // 記錄成功、失敗和總和筆數
        int success = 0;
        int failed = 0;
        int total;

        // 取得 DataFormatter ，用於取 Cell 值
        DataFormatter formatter = new DataFormatter();

        // 取得 excel 檔案
        File excelFile = source.toFile();

        // 開始處理資訊
        try (Workbook workbook = WorkbookFactory.create(excelFile, null, true)) {
            // 取得頁籤 (該案例基本上只有一個)
            Sheet sheet = workbook.getSheetAt(0);

            // 取得最後一列 index
            int lastRowNum = sheet.getLastRowNum();

            // 遍歷每一列
            row:
            for (int i = 1; i <= lastRowNum; i++) {
                // 資料表名稱, oracle SQL語法, informix SQL語法
                String tableName = "";
                String ifxSql = "";
                String oraSql = "";

                // 取得該 index Row
                Row row = sheet.getRow(i);

                if (row == null) { // 空白列
                    // 記錄錯誤
                    buildResultLog(resultLog, i + 1, tableName, LOG_LOST_ROW);
                    // 失敗記數
                    failed++;

                    // 跳下一列
                    continue;
                }

                // 遍歷三個欄位 (0: 資料表名稱, 1: oracle SQL語法, 2: informix SQL語法
                for (int j = 0; j < 3; j++) {
                    // 取得該 index Cell
                    Cell cell = row.getCell(j);

                    if (cell == null) { // 空白欄
                        // 記錄錯誤
                        buildResultLog(resultLog, i + 1, tableName, LOG_LOST_CELL);
                        // 失敗記數
                        failed++;

                        // 跳下一列
                        continue row;
                    }

                    // 取得關鍵字串
                    switch (j) {
                        case 0 -> { // 資料表名稱
                            tableName = clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(tableName)) { // 資料表名稱 為空
                                // 記錄錯誤
                                buildResultLog(resultLog, i + 1, tableName, LOG_LOST_TABLE_NAME);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }

                            // 取得系統名，只賦值一次，依照 資料表名稱 習慣，第一個底線前應為系統名
                            systemName = DEFAULT_SYSTEM_NAME.equals(systemName) ? tableName.split("_")[0] : systemName;
                        }

                        case 1 -> { // oracle SQL語法 (移除換行符與非法空格)
                            oraSql = clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(oraSql)) { // oracle SQL語法 為空
                                // 記錄錯誤
                                buildResultLog(resultLog, i + 1, tableName, LOG_LOST_SQL);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }
                        }

                        case 2 -> { // informix SQL語法 (移除換行符與非法空格)
                            ifxSql = clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(ifxSql)) { // informix SQL語法 為空
                                // 記錄錯誤
                                buildResultLog(resultLog, i + 1, tableName, LOG_LOST_SQL);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }
                        }
                    }
                }

                // 組裝結果字串
                buildResult(result, tableName, oraSql, ifxSql);
                // 成功記數
                success++;
            }

            // 取得 yml 檔路徑
            resultPath = resultDir.resolve(systemName + ".yml");
            // 輸出結果 yml 檔
            Files.writeString(resultPath, result.toString(), StandardCharsets.UTF_8);
            log.info("============================> {} yml 輸出完成！", systemName);

            // 統計成功失敗與總數
            total = success + failed;

            // resultLog 有內容
            if (!resultLog.isEmpty()) {
                // 加上統計結果
                addCountToLog(resultLog, failed, success, total);

                // 取得 txt 檔路徑
                exceptionPath = exceptionDir.resolve(systemName + "_exception.txt");
                // 輸出錯誤結果 txt 檔
                Files.writeString(exceptionPath, resultLog.toString(), StandardCharsets.UTF_8);
                log.info("============================> {} txt 輸出完成！",  systemName);
            }
        } catch (Exception e) {
            log.error("轉換失敗: ", e);
        }

        return resultPath;
    }

    /**
     * 驗證 yml 檔是否合法，若格式錯誤拋出例外
     *
     * @param ymlPath
     *          yml 檔路徑
     */
    private static void verifyYml(Path ymlPath) {
        // 取得系統名
        String systemName = getBaseName(ymlPath.getFileName().toString());

        try (InputStream input = Files.newInputStream(ymlPath)) {
            Yaml yml = new Yaml();

            // 讀取成功為格式正確
            yml.load(input);

            log.info("============================> {} yml 格式正確！", systemName);
        } catch (Exception e) {
            log.error("驗證 {} yml 發生錯誤: ", systemName, e);
        }
    }

    /**
     * 組裝 table 資訊
     *
     * @param result
     *          當前結果字串
     * @param tableName
     *          資料表名稱
     * @param oraSql
     *          oracle SQL語法
     * @param ifxSql
     *          informix SQL語法
     */
    private static void buildResult(StringBuilder result,
                                    String tableName,
                                    String oraSql,
                                    String ifxSql) {
        // 資料表名稱
        result.append(YML_TABLE_NAME_ROW);
        result.append(tableName).append("\n");

        // informix SQL語法
        result.append(YML_IFX_SQL_ROW);
        result.append(ifxSql).append("\n");

        // oracle SQL語法
        result.append(YML_ORA_SQL_ROW);
        result.append(oraSql).append("\n");
    }

    /**
     * 清洗原始字串，移除換行與非法空格，再去除頭尾空格
     *
     * @param oriStr
     *          原始字串
     * @return cleanStr
     *          合法字串 (基於合法 tableName 不會換行，僅用於去除頭尾空格)
     */
    private static String clearStr(String oriStr) {
        return oriStr
                .replaceAll("[\\r\\n\\t\\u00A0]+", " ")
                .trim();
    }

    /**
     * 錯誤記錄
     *
     * @param resultLog
     *          當前錯誤記錄字串
     * @param rowNum
     *          excel 列號
     * @param tableName
     *          資料表名稱
     * @param description
     *          錯誤內容描述
     */
    private static void buildResultLog(StringBuilder resultLog,
                                       int rowNum,
                                       String tableName,
                                       String description) {
        // excel 列號
        resultLog.append(LOG_ROW_NUM_ROW);
        resultLog.append(rowNum).append("\n");

        // 資料表名稱
        resultLog.append(LOG_TABLE_NAME_ROW);
        resultLog.append(tableName).append("\n");

        // 錯誤內容描述
        resultLog.append(LOG_DESCRIPTION_ROW);
        resultLog.append(description).append("\n");
    }

    /**
     * 加上統計結果
     *
     * @param resultLog
     *          當前錯誤記錄字串
     * @param failed
     *          失敗數
     * @param success
     *          成功數
     * @param total
     *          總數
     */
    private static void addCountToLog(StringBuilder resultLog,
                                      int failed,
                                      int success,
                                      int total) {
        // 失敗數
        resultLog.append(LOG_FAILED_ROW);
        resultLog.append(failed).append("\n");

        // 成功數
        resultLog.append(LOG_SUCCESS_ROW);
        resultLog.append(success).append("\n");

        // 總數
        resultLog.append(LOG_TOTAL_ROW);
        resultLog.append(total).append("\n");
    }

    /**
     * 取得純檔名
     *
     * @param fileName
     *          含附檔名檔名
     * @return 純檔名
     */
    private static String getBaseName(String fileName){
        return fileName.replaceAll("\\.[^.]+$", "");
    }

}
