package maddog.sqlexceltoyml.validator;

import lombok.extern.slf4j.Slf4j;
import maddog.sqlexceltoyml.util.FileNameUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class YmlValidator {

    /**
     * 驗證 yml 檔是否合法
     *
     * @param ymlPath
     *          yml 檔路徑
     */
    public void validate(Path ymlPath) {
        // 取得系統名
        String systemName = FileNameUtil.getBaseName(ymlPath.getFileName().toString());

        try (InputStream input = Files.newInputStream(ymlPath)) {
            Yaml yml = new Yaml();

            // 讀取成功為格式正確
            yml.load(input);

            log.info("============================> {} yml 格式正確！", systemName);
        } catch (Exception e) {
            log.error("驗證 {} yml 發生錯誤: ", systemName, e);
        }
    }

}
