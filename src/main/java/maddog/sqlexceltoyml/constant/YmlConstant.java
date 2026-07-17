package maddog.sqlexceltoyml.constant;

/**
 * 顯示常數
 */
public final class YmlConstant {

    private YmlConstant(){}

    /**
     * 資料表名稱預設
     */
    public static final String DEFAULT_SYSTEM_NAME = "資料表名稱無系統名";

    /**
     * yml 標題 (換行縮排 2 空白)
     */
    public static final String YML_HEADER = "compare-sql-setting:\n  tables:\n";

    /**
     * yml table-name 列固定文字 (縮排 4 空白)
     */
    public static final String YML_TABLE_NAME_ROW = "    - table-name: ";

    /**
     * yml informix 語法列固定文字 (縮排 6 空白)
     */
    public static final String YML_IFX_SQL_ROW = "      ifx-sql: ";

    /**
     * yml oracle SQL 列固定文字 (縮排 6 空白)
     */
    public static final String YML_ORA_SQL_ROW = "      ora-sql: ";

    /**
     * txt Log row-num 列固定文字
     */
    public static final String LOG_ROW_NUM_ROW = "- row-num: ";

    /**
     * txt Log table-name 列固定文字 (縮排 4 空白)
     */
    public static final String LOG_TABLE_NAME_ROW = "    table-name: ";

    /**
     * txt Log description 列固定文字 (縮排 4 空白)
     */
    public static final String LOG_DESCRIPTION_ROW = "    description: ";

    /**
     * txt Log failed 列固定文字
     */
    public static final String LOG_FAILED_ROW = "failed: ";

    /**
     * txt Log success 列固定文字
     */
    public static final String LOG_SUCCESS_ROW = "success: ";

    /**
     * txt Log total 列固定文字
     */
    public static final String LOG_TOTAL_ROW = "total: ";

    /**
     * txt Log description "資料表名稱為空"
     */
    public static final String LOG_LOST_TABLE_NAME = "資料表名稱為空";

    /**
     * txt Log description "oracle SQL 為空"
     */
    public static final String LOG_LOST_ORA_SQL = "oracle SQL 為空";

    /**
     * txt Log description "informix SQL 為空"
     */
    public static final String LOG_LOST_IFX_SQL = "informix SQL 為空";
    
}
