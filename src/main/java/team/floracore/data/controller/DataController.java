package team.floracore.data.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.floracore.data.common.BaseResponse;
import team.floracore.data.common.ResultUtils;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.TranslationInfo;

import java.util.List;

/**
 * Data 接口控制器
 *
 * @author xLikeWATCHDOG
 */
@RestController
@RequestMapping("/data")
@Slf4j
public class DataController {
    @Resource
    private CrowdinService crowdinService;

    @GetMapping("/translations")
    public BaseResponse<List<TranslationInfo>> getTranslationInfo() {
        return ResultUtils.success(crowdinService.getTranslationInfo());
    }
}
