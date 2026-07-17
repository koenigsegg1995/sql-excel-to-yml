package maddog.sqlexceltoyml.util;

/**
 * SQL 工具
 */
public final class SqlUtil {

    private SqlUtil() {}

    /**
     * 清洗原始 SQL ，移除換行與非法空格，再去除頭尾空格
     *
     * @param oriSql
     *          原始字串
     * @return cleanSql
     *          合法字串 (基於合法 tableName 不會換行，僅用於去除頭尾空格)
     */
    public static String clearSql (String oriSql) {
        return oriSql
                .replaceAll("[\\r\\n\\t\\u00A0]+", " ")
                .trim();
    }

}
