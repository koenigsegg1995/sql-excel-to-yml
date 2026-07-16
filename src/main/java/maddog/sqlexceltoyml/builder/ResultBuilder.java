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
    public void build (StringBuilder result,
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

}
