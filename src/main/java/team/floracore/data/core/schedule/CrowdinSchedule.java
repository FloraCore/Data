package team.floracore.data.core.schedule;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
@Async
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

    @PostConstruct
    public void init() {
        // 项目启动时执行一次任务
        executeTask();
    }
}
