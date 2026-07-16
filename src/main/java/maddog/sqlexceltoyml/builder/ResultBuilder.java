package maddog.sqlexceltoyml.builder;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * 組裝結果
 */
public class ResultBuilder {

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
    public void buildResult(StringBuilder result,
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
    public void addCountToLog(StringBuilder resultLog,
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

}
