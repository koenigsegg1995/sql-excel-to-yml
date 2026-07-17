package maddog.sqlexceltoyml.util;

/**
 * 檔名工具
 */
public final class FileNameUtil {

    private FileNameUtil() {}

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

}
