package team.floracore.data.service;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.DownloadLink;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.languages.model.Language;
import com.crowdin.client.reports.model.ReportStatus;
import com.crowdin.client.translationstatus.model.LanguageProgress;
import team.floracore.data.utils.crowdin.Contributor;
import team.floracore.data.utils.crowdin.FileType;
import team.floracore.data.utils.crowdin.TranslationInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Crowdin服务接口
 *
 * @author xLikeWATCHDOG
 */
public interface CrowdinService {
    void refreshLanguages() throws IOException;

    Map<String, TranslationInfo> getLanguages();

    void downloadTranslationFile(FileType fileType) throws IOException;

    List<TranslationInfo> getTranslationInfoList();

    TranslationInfo getTranslationInfo(String id);

    void processLanguageProgress(FileType fileType);

    void processContributors(List<Contributor> contributors);

    void updateTranslationInfo();

    Language getLanguage(String languageId);

    List<ResponseObject<LanguageProgress>> getLanguageProgress(FileType fileType);

    List<Contributor> getContributors() throws IOException;

    DownloadLink downloadReport();

    ReportStatus generateReport();

    Client getClient();

    DownloadLink getTranslationFileDownloadLink(String targetLanguageId, long id);
}
