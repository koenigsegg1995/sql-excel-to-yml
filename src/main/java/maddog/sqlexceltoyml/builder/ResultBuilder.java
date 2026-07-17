package maddog.sqlexceltoyml.builder;

import static maddog.sqlexceltoyml.constant.YmlConstant.*;

/**
 * 組裝結果
 */
public class ResultBuilder {

    /**
     * 當前結果字串
     */
    private final StringBuilder result;

    /**
     * 初始化取得 StringBuilder 容器並將 HEADER 加入
     */
    public ResultBuilder() {
        result = new StringBuilder();
        result.append(YML_HEADER);
    }

    /**
     * 組裝 table 資訊
     *
     * @param tableName
     *          資料表名稱
     * @param oraSql
     *          oracle SQL語法
     * @param ifxSql
     *          informix SQL語法
     */
    public void addResult(String tableName,
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
     * 取得結果字串
     *
     * @return 結果字串
     */
    public String getResult() {
        return result.toString();
    }

}
