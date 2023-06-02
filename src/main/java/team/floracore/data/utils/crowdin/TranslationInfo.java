package team.floracore.data.utils.crowdin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 翻译信息
 *
 * @author xLikeWATCHDOG
 */
@Data
public class TranslationInfo {
    private String name;
    private String languageId;
    private String localeTag;
    private int progress;
    private int progressWeb;
    private List<ContributorInfo> contributors = new ArrayList<>();

    @Data
    public static class ContributorInfo {
        private String name;
        private int translated;
    }
}
