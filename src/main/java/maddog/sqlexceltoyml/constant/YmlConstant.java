package maddog.sqlexceltoyml.constant;

/**
 * yml 顯示常數
 */
public final class YmlConstant {

    /**
     * 資料表名稱預設
     */
    public static final String DEFAULT_SYSTEM_NAME = "資料表名稱無系統名";

    /**
     * yml table-name 列固定文字 (縮排 4 空白)
     */
    public static final String TABLE_NAME_ROW = "    - table-name: ";

    /**
     * yml informix 語法列固定文字 (縮排 6 空白)
     */
    public static final String IFX_SQL_ROW = "      ifx-sql: ";

    /**
     * yml oracle SQL 列固定文字 (縮排 6 空白)
     */
    public static final String ORA_SQL_ROW = "      ora-sql: ";

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
     * txt Log description "空白列"
     */
    public static final String LOST_ROW = "空白列";

    /**
     * txt Log description "資料表名稱為空"
     */
    public static final String LOST_TABLE_NAME = "資料表名稱為空";

    /**
     * txt Log description "欄位為空"
     */
    public static final String LOST_CELL = "欄位為空";

    /**
     * txt Log description "SQL 為空"
     */
    public static final String LOST_SQL = "SQL 為空";
    
}
