package team.floracore.data.crowdin;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.translationstatus.model.LanguageProgress;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.FileType;

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
        for (ResponseObject<LanguageProgress> languageProgress : crowdinService.getLanguageProgress(FileType.PLUGIN)) {
            languageProgress.getData().getLanguageId();
        }
    }
}
