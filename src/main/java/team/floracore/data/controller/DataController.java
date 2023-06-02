package team.floracore.data.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.floracore.data.common.BaseResponse;
import team.floracore.data.common.ResultUtils;
import team.floracore.data.service.CrowdinService;
import team.floracore.data.utils.crowdin.TranslationInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        return ResultUtils.success(crowdinService.getTranslationInfoList());
    }


    @GetMapping("/names")
    public void fileDownload(HttpServletResponse response) {
        try {
            //图片的绝对路径（工程路径+图片的相对路径）
            String path = "./data/names.csv";
            //创建输入流
            InputStream is = new FileInputStream(path);
            //创建字节数组，获取当前文件中所有的字节数
            byte[] bytes = new byte[is.available()];
            //将流读到字节数组中
            is.read(bytes);
            //设置响应头信息，Content-Disposition响应头表示收到的数据怎么处理（固定），attachment表示下载使用（固定），filename指定下载的文件名（下载时会在客户端显示该名字）
            response.addHeader("Content-Disposition", "attachment;filename=names.csv");
            //创建输出流
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            //关闭资源
            is.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
