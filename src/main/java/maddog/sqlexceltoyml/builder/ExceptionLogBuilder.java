package maddog.sqlexceltoyml.builder;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * 組裝錯誤記錄
 */
public class ExceptionLogBuilder {

    /**
     * 錯誤記錄
     *
     * @param exceptionLog
     *          當前錯誤記錄字串
     * @param rowNum
     *          excel 列號
     * @param tableName
     *          資料表名稱
     * @param description
     *          錯誤內容描述
     */
    public void build (StringBuilder exceptionLog,
                       int rowNum,
                       String tableName,
                       String description) {
        // excel 列號
        exceptionLog.append(LOG_ROW_NUM_ROW);
        exceptionLog.append(rowNum).append("\n");

        // 資料表名稱
        exceptionLog.append(LOG_TABLE_NAME_ROW);
        exceptionLog.append(tableName).append("\n");

        // 錯誤內容描述
        exceptionLog.append(LOG_DESCRIPTION_ROW);
        exceptionLog.append(description).append("\n");
    }

    /**
     * 加上統計結果
     *
     * @param exceptionLog
     *          當前錯誤記錄字串
     * @param failed
     *          失敗數
     * @param success
     *          成功數
     * @param total
     *          總數
     */
    public void addCountToLog (StringBuilder exceptionLog,
                               int failed,
                               int success,
                               int total) {
        // 失敗數
        exceptionLog.append(LOG_FAILED_ROW);
        exceptionLog.append(failed).append("\n");

        // 成功數
        exceptionLog.append(LOG_SUCCESS_ROW);
        exceptionLog.append(success).append("\n");

        // 總數
        exceptionLog.append(LOG_TOTAL_ROW);
        exceptionLog.append(total).append("\n");
    }

}
