package team.floracore.data.crowdin;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.FileType;

import java.io.IOException;

/**
 * Crowdin测试类
 *
 * @author xLikeWATCHDOG
 */
@SpringBootTest
public class CrowdinTest {
    @Resource
    private CrowdinService crowdinService;

    @Test
    void contextLoads() {
        for (FileType value : FileType.values()) {
            try {
                crowdinService.downloadTranslationFile(value);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
