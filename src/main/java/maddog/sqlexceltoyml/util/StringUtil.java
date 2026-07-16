package maddog.sqlexceltoyml.util;

/**
 * 工具
 */
public final class StringUtil {

    private StringUtil() {}

    /**
     * 取得純檔名
     *
     * @param fileName
     *          含附檔名檔名
     * @return 純檔名
     */
    public static String getBaseName (String fileName) {
        return fileName.replaceAll("\\.[^.]+$", "");
    }

    /**
     * 清洗原始字串，移除換行與非法空格，再去除頭尾空格
     *
     * @param oriStr
     *          原始字串
     * @return cleanStr
     *          合法字串 (基於合法 tableName 不會換行，僅用於去除頭尾空格)
     */
    public static String clearStr (String oriStr) {
        return oriStr
                .replaceAll("[\\r\\n\\t\\u00A0]+", " ")
                .trim();
    }

}
