package team.floracore.data.utils.crowdin;

import lombok.Data;

/**
 * 贡献者信息
 *
 * @author xLikeWATCHDOG
 */

@Data
public class Contributor {
    private User user;
    private Language[] languages;
    private int translated;
    private int approved;
    private int voted;
    private int positiveVotes;
    private int negativeVotes;
    private int winning;

    @Data
    public static class User {
        private String id;
        private String username;
        private String fullName;
        private String avatarUrl;
        private String joined;
    }

    @Data
    public static class Language {
        private String id;
        private String name;
    }
}
