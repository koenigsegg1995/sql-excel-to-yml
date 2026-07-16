package maddog.sqlexceltoyml.builder;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * 組裝錯誤記錄
 */
public class ExceptionLogBuilder {

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
    public void buildResultLog(StringBuilder resultLog,
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

}
