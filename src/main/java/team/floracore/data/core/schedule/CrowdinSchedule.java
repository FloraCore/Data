package team.floracore.data.core.schedule;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team.floracore.data.service.CrowdinService;

import java.io.IOException;

/**
 * Crowdin 任务事件
 *
 * @author xLikeWATCHDOG
 */
@Component
@Slf4j
public class CrowdinSchedule {
    @Resource
    private CrowdinService crowdinService;

    // 每隔10分钟执行一次
    @Scheduled(fixedRate = 600000)
    public void executeTask() {
        try {
            crowdinService.refreshLanguages();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
