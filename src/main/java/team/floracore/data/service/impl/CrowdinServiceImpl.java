package team.floracore.data.service.impl;

import com.crowdin.client.Client;
import com.crowdin.client.core.model.Credentials;
import com.crowdin.client.core.model.DownloadLink;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.languages.model.Language;
import com.crowdin.client.reports.model.ReportStatus;
import com.crowdin.client.reports.model.ReportsFormat;
import com.crowdin.client.reports.model.TopMembersGenerateReportRequest;
import com.crowdin.client.reports.model.Unit;
import com.crowdin.client.translations.model.ExportProjectTranslationRequest;
import com.crowdin.client.translationstatus.model.LanguageProgress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.Contributor;
import team.floracore.data.utils.crowdin.FileType;
import team.floracore.data.utils.crowdin.TranslationInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Crowdin服务接口实现
 *
 * @author xLikeWATCHDOG
 */
@Service
@Slf4j
public class CrowdinServiceImpl implements CrowdinService {
    private static final Map<String, TranslationInfo> languages = new HashMap<>();
    @Value("${crowdin.token:null}")
    private String token;
    @Value("${crowdin.project.id:582143}")
    private Long projectId;
    @Value("${crowdin.plugin.id:5}")
    private Long pluginId;
    @Value("${crowdin.web.id:11}")
    private Long webId;

    @Override
    public void refreshLanguages() throws IOException {
        languages.clear();
        for (FileType value : FileType.values()) {
            processLanguageProgress(value);
            File directory = new File("./translations/" + value.name().toLowerCase());
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }
        processContributors(getContributors());
        updateTranslationInfo();
        for (FileType value : FileType.values()) {
            downloadTranslationFile(value);
        }
    }

    @Override
    public Map<String, TranslationInfo> getLanguages() {
        return languages;
    }

    @Override
    public void downloadTranslationFile(FileType fileType) throws IOException {
        for (String key : languages.keySet()) {
            TranslationInfo translationInfo = languages.get(key);
            String fileName = translationInfo.getLocaleTag() + ".";
            long id;
            if (fileType == FileType.PLUGIN) {
                id = pluginId;
                fileName = fileName + "properties";
            } else if (fileType == FileType.WEB) {
                id = webId;
                fileName = fileName + "json";
            } else {
                throw new IllegalArgumentException("Invalid FileType");
            }
            String DOWNLOAD_URL = getTranslationFileDownloadLink(translationInfo.getLanguageId(), id).getUrl();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(DOWNLOAD_URL)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    File file = new File("./translations/" + fileType.name().toLowerCase(), fileName);
                    try (InputStream inputStream = responseBody.byteStream();
                         FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();
                    } catch (IOException e) {
                        throw new IOException("Failed to write file!", e);
                    }
                    log.info(fileName + "Translation files downloaded successfully! File Type:" + fileType.name());
                } else {
                    throw new RuntimeException("Failed to download file! HTTP response code:" + response.code());
                }
            } catch (IOException e) {
                throw new IOException("Exception in downloading file:" + e.getMessage(), e);
            }
        }

    }

    @Override
    public List<TranslationInfo> getTranslationInfoList() {
        return languages.values().stream().toList();
    }

    @Override
    public TranslationInfo getTranslationInfo(String id) {
        return languages.get(id);
    }

    @Override
    public void processLanguageProgress(FileType fileType) {
        for (ResponseObject<LanguageProgress> response : getLanguageProgress(fileType)) {
            LanguageProgress languageProgress = response.getData();
            String id = languageProgress.getLanguageId();
            int percent = languageProgress.getTranslationProgress();
            TranslationInfo translationInfo = languages.computeIfAbsent(id, k -> new TranslationInfo());
            translationInfo.setLanguageId(id);
            if (fileType == FileType.PLUGIN) {
                translationInfo.setProgress(percent);
            } else if (fileType == FileType.WEB) {
                translationInfo.setProgressWeb(percent);
            }
        }
    }

    @Override
    public void processContributors(List<Contributor> contributors) {
        for (Contributor contributor : contributors) {
            if (contributor.getTranslated() >= 30) {
                for (Contributor.Language language : contributor.getLanguages()) {
                    TranslationInfo translationInfo = languages.get(language.getId());
                    if (translationInfo != null) {
                        TranslationInfo.ContributorInfo contributorInfo = new TranslationInfo.ContributorInfo();
                        contributorInfo.setName(contributor.getUser().getUsername());
                        contributorInfo.setTranslated(contributor.getTranslated());
                        translationInfo.getContributors().add(contributorInfo);
                    }
                }
            }
        }
    }

    @Override
    public void updateTranslationInfo() {
        for (TranslationInfo translationInfo : languages.values()) {
            String languageId = translationInfo.getLanguageId();
            Language language = getLanguage(languageId);
            String name = language.getName();
            String localeTag = language.getLocale().replace("-", "_");
            translationInfo.setName(name);
            translationInfo.setLocaleTag(localeTag);
        }
    }


    @Override
    public Language getLanguage(String languageId) {
        return getClient().getLanguagesApi().getLanguage(languageId).getData();
    }

    @Override
    public List<ResponseObject<LanguageProgress>> getLanguageProgress(FileType fileType) {
        switch (fileType) {
            case PLUGIN -> {
                return getClient().getTranslationStatusApi().getFileProgress(projectId, pluginId, 500, 0).getData();
            }
            case WEB -> {
                return getClient().getTranslationStatusApi().getFileProgress(projectId, webId, 500, 0).getData();
            }
        }
        return null;
    }

    @Override
    public List<Contributor> getContributors() throws IOException {
        OkHttpClient client = new OkHttpClient();
        String reportUrl = downloadReport().getUrl();
        Request request = new Request.Builder()
                .url(reportUrl)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String responseData = responseBody.string();
                // 处理响应数据
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                JsonArray dataArray = jsonObject.getAsJsonArray("data");
                List<Contributor> contributors = new ArrayList<>();

                if (dataArray != null) {
                    for (JsonElement element : dataArray) {
                        JsonObject contributorObject = element.getAsJsonObject();

                        // 重用现有的JsonObject和Contributor对象
                        contributorObject = gson.fromJson(contributorObject, JsonObject.class);
                        Contributor contributor = gson.fromJson(contributorObject, Contributor.class);

                        contributors.add(contributor);
                    }
                }

                // 使用List.addAll()一次性添加所有Contributor对象
                contributors.addAll(Objects.requireNonNull(gson.fromJson(dataArray, new TypeToken<List<Contributor>>() {
                }.getType())));
                return contributors;
            } else {
                throw new RuntimeException("Request failed:" + response.code() + " " + response.message());
            }
        } catch (IOException e) {
            throw new IOException("Request exception:" + e.getMessage());
        }
    }

    @Override
    public DownloadLink downloadReport() {
        ReportStatus reportStatus = generateReport();
        String identifier = reportStatus.getIdentifier();
        return getClient().getReportsApi().downloadReport(projectId, identifier).getData();
    }

    @Override
    public ReportStatus generateReport() {
        TopMembersGenerateReportRequest topMembersGenerateReportRequest = new TopMembersGenerateReportRequest();
        topMembersGenerateReportRequest.setName("top-members");
        TopMembersGenerateReportRequest.Schema schema = new TopMembersGenerateReportRequest.Schema();
        schema.setUnit(Unit.STRINGS);
        schema.setFormat(ReportsFormat.JSON);
        topMembersGenerateReportRequest.setSchema(schema);
        return getClient().getReportsApi().generateReport(projectId, topMembersGenerateReportRequest).getData();
    }

    @Override
    public Client getClient() {
        Credentials credentials = new Credentials(token, null);
        return new Client(credentials);
    }

    @Override
    public DownloadLink getTranslationFileDownloadLink(String targetLanguageId, long id) {
        ExportProjectTranslationRequest exportProjectTranslationRequest = new ExportProjectTranslationRequest();
        exportProjectTranslationRequest.setTargetLanguageId(targetLanguageId);
        exportProjectTranslationRequest.setFileIds(List.of(id));
        exportProjectTranslationRequest.setSkipUntranslatedStrings(true);
        return getClient().getTranslationsApi().exportProjectTranslation(projectId, exportProjectTranslationRequest).getData();
    }
}
