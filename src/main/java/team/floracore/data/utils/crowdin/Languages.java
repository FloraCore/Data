package team.floracore.data.utils.crowdin;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 语言列表
 *
 * @author xLikeWATCHDOG
 */
@Data
public class Languages {
    public Map<String, TranslationInfo> languages = new HashMap<>();
    public int cacheMaxAge;
}
