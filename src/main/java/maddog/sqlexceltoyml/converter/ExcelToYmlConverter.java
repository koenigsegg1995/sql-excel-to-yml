package maddog.sqlexceltoyml.converter;

import lombok.extern.slf4j.Slf4j;
import maddog.sqlexceltoyml.builder.ExceptionLogBuilder;
import maddog.sqlexceltoyml.builder.ResultBuilder;
import maddog.sqlexceltoyml.util.SqlUtil;
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
    public Path convert (Path source,
                         Path resultDir,
                         Path exceptionDir) {
        // yml 檔路徑
        Path resultPath = null;
        // txt 檔路徑
        Path exceptionPath;

        // 系統名
        String systemName = DEFAULT_SYSTEM_NAME;

        // 組裝結果
        ResultBuilder resultBuilder = new ResultBuilder();
        // 組裝結果紀錄
        ExceptionLogBuilder exceptionLogBuilder = new ExceptionLogBuilder();

        // 成功、失敗和總數
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
            for (int i = 1; i <= lastRowNum; i++) {
                // 資料表名稱, oracle SQL語法, informix SQL語法
                String tableName = "";
                String ifxSql = "";
                String oraSql = "";

                // 取得該 index Row
                Row row = sheet.getRow(i);

                if (row == null) { // 空白列不計算
                    // 跳下一列
                    continue;
                }

                // 空白欄數
                int blankCellCount = 0;
                // 錯誤描述
                StringBuilder description = new StringBuilder();

                // 遍歷三個欄位 (0: 資料表名稱, 1: oracle SQL語法, 2: informix SQL語法
                for (int j = 0; j < 3; j++) {
                    // 取得該 index Cell
                    Cell cell = row.getCell(j);

                    // 取得關鍵字串
                    switch (j) {
                        case 0 -> { // 資料表名稱
                            if (cell == null) { // 空白欄
                                // 記錄錯誤描述
                                description.append(LOG_LOST_TABLE_NAME).append(" ");

                                // 空白計數
                                blankCellCount++;
                            } else {
                                tableName = SqlUtil.cleanSql(formatter.formatCellValue(cell));

                                if (StringUtils.isEmpty(tableName)) { // 資料表名稱 為空
                                    // 記錄錯誤描述
                                    description.append(LOG_LOST_TABLE_NAME).append(" ");

                                    // 空白計數
                                    blankCellCount++;
                                }

                                // 取得系統名，只賦值一次，依照 資料表名稱 習慣，第一個底線前應為系統名
                                systemName = DEFAULT_SYSTEM_NAME.equals(systemName) ? tableName.split("_")[0] : systemName;
                            }
                        }

                        case 1 -> { // oracle SQL語法 (移除換行符與非法空格)
                            if (cell == null) { // 空白欄
                                // 記錄錯誤描述
                                description.append(LOG_LOST_ORA_SQL).append(" ");

                                // 空白計數
                                blankCellCount++;
                            } else {
                                oraSql = SqlUtil.cleanSql(formatter.formatCellValue(cell));

                                if (StringUtils.isEmpty(oraSql)) { // oracle SQL語法 為空
                                    // 記錄錯誤描述
                                    description.append(LOG_LOST_ORA_SQL).append(" ");

                                    // 空白計數
                                    blankCellCount++;
                                }
                            }
                        }

                        case 2 -> { // informix SQL語法 (移除換行符與非法空格)
                            if (cell == null) { // 空白欄
                                // 記錄錯誤描述
                                description.append(LOG_LOST_IFX_SQL).append(" ");

                                // 空白計數
                                blankCellCount++;
                            } else {
                                ifxSql = SqlUtil.cleanSql(formatter.formatCellValue(cell));

                                if (StringUtils.isEmpty(ifxSql)) { // informix SQL語法 為空
                                    // 記錄錯誤描述
                                    description.append(LOG_LOST_IFX_SQL).append(" ");

                                    // 空白計數
                                    blankCellCount++;
                                }
                            }
                        }
                    }
                }

                if (blankCellCount == 0) { // 無錯誤
                    // 組裝結果字串
                    resultBuilder.addResult(tableName, oraSql, ifxSql);

                    // 成功計數
                    success++;
                } else if (blankCellCount != 3) { // 非空白列
                    // 記錄錯誤
                    exceptionLogBuilder.addLog(i + 1, tableName, description);

                    // 失敗計數
                    failed++;
                }
            }

            // 取得 yml 檔路徑
            resultPath = resultDir.resolve(systemName + ".yml");
            // 輸出結果 yml 檔
            Files.writeString(resultPath, resultBuilder.getResult(), StandardCharsets.UTF_8);
            log.info("============================> {} yml 輸出完成！", systemName);

            // 統計成功失敗總數
            total = success + failed;

            // exceptionLog 有內容
            if (!exceptionLogBuilder.isEmpty()) {
                log.warn("{} 有錯誤，將輸出 exception txt", systemName);
                // 加上統計結果
                exceptionLogBuilder.addCount(failed, success, total);

                // 取得 txt 檔路徑
                exceptionPath = exceptionDir.resolve(systemName + "_exception.txt");
                // 輸出錯誤結果 txt 檔
                Files.writeString(exceptionPath, exceptionLogBuilder.getLog(), StandardCharsets.UTF_8);
                log.info("============================> {} txt 輸出完成！",  systemName);
            }
        } catch (Exception e) {
            log.error("轉換失敗: ", e);
        }

        return resultPath;
    }

}
