package team.floracore.data.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.FileType;
import team.floracore.data.utils.crowdin.TranslationInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 翻译文件下载控制器
 *
 * @author xLikeWATCHDOG
 */
@RestController
@RequestMapping("/translations")
@Slf4j
public class TranslationController {
    @Resource
    private CrowdinService crowdinService;

    @GetMapping("/{localeId}")
    public void handleWithoutType(@PathVariable("localeId") String localeId,
                                  HttpServletResponse response) {
        handleWithType(FileType.PLUGIN.name(), localeId, response);
    }

    @GetMapping("/{fileType}/{localeId}")
    public void handleWithType(
            @PathVariable("fileType") String fileType,
            @PathVariable("localeId") String localeId,
            HttpServletResponse response
    ) {
        if (StringUtils.isAnyBlank(fileType, localeId)) {
            throw new RuntimeException("Parameter is empty");
        }
        try {
            FileType ft = FileType.valueOf(fileType.toUpperCase());
            TranslationInfo translationInfo = crowdinService.getTranslationInfo(localeId);
            if (translationInfo == null) {
                throw new RuntimeException("No target translation language exists");
            }
            String fileName = translationInfo.getLocaleTag() + ".";
            if (ft == FileType.PLUGIN) {
                fileName = fileName + "properties";
            } else if (ft == FileType.WEB) {
                fileName = fileName + "json";
            } else {
                throw new IllegalArgumentException("Invalid FileType");
            }
            try {
                //图片的绝对路径（工程路径+图片的相对路径）
                String path = "./translations/" + ft.name().toLowerCase() + "/" + fileName;
                //创建输入流
                InputStream is = new FileInputStream(path);
                //创建字节数组，获取当前文件中所有的字节数
                byte[] bytes = new byte[is.available()];
                //将流读到字节数组中
                is.read(bytes);
                //设置响应头信息，Content-Disposition响应头表示收到的数据怎么处理（固定），attachment表示下载使用（固定），filename指定下载的文件名（下载时会在客户端显示该名字）
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
                //创建输出流
                OutputStream out = response.getOutputStream();
                out.write(bytes);
                //关闭资源
                is.close();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Translation file type does not exist");
        }
    }
}
