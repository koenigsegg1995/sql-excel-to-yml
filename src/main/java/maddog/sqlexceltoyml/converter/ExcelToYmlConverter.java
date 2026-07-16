package maddog.sqlexceltoyml.converter;

import lombok.extern.slf4j.Slf4j;
import maddog.sqlexceltoyml.builder.ExceptionLogBuilder;
import maddog.sqlexceltoyml.builder.ResultBuilder;
import maddog.sqlexceltoyml.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * 核心轉換
 */
@Slf4j
public class ExcelToYmlConverter {

    /**
     * 組裝結果
     */
    private final ResultBuilder resultBuilder;

    /**
     * 組裝錯誤記錄
     */
    private final ExceptionLogBuilder exceptionLogBuilder;

    public ExcelToYmlConverter(){
        resultBuilder = new ResultBuilder();
        exceptionLogBuilder = new ExceptionLogBuilder();
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
    public Path transform(Path source,
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
                    exceptionLogBuilder.buildResultLog(resultLog, i + 1, tableName, LOG_LOST_ROW);
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
                        exceptionLogBuilder.buildResultLog(resultLog, i + 1, tableName, LOG_LOST_CELL);
                        // 失敗記數
                        failed++;

                        // 跳下一列
                        continue row;
                    }

                    // 取得關鍵字串
                    switch (j) {
                        case 0 -> { // 資料表名稱
                            tableName = Util.clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(tableName)) { // 資料表名稱 為空
                                // 記錄錯誤
                                exceptionLogBuilder.buildResultLog(resultLog, i + 1, tableName, LOG_LOST_TABLE_NAME);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }

                            // 取得系統名，只賦值一次，依照 資料表名稱 習慣，第一個底線前應為系統名
                            systemName = DEFAULT_SYSTEM_NAME.equals(systemName) ? tableName.split("_")[0] : systemName;
                        }

                        case 1 -> { // oracle SQL語法 (移除換行符與非法空格)
                            oraSql = Util.clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(oraSql)) { // oracle SQL語法 為空
                                // 記錄錯誤
                                exceptionLogBuilder.buildResultLog(resultLog, i + 1, tableName, LOG_LOST_SQL);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }
                        }

                        case 2 -> { // informix SQL語法 (移除換行符與非法空格)
                            ifxSql = Util.clearStr(formatter.formatCellValue(cell));

                            if (StringUtils.isEmpty(ifxSql)) { // informix SQL語法 為空
                                // 記錄錯誤
                                exceptionLogBuilder.buildResultLog(resultLog, i + 1, tableName, LOG_LOST_SQL);
                                // 失敗記數
                                failed++;

                                // 跳下一列
                                continue row;
                            }
                        }
                    }
                }

                // 組裝結果字串
                resultBuilder.buildResult(result, tableName, oraSql, ifxSql);
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
                resultBuilder.addCountToLog(resultLog, failed, success, total);

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

}
