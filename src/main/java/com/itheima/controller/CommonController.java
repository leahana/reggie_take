package com.itheima.controller;


import com.itheima.common.R;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传和下载
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        //file 临时文件 转存指定位置 完成请求后暂存文件删除

        //记录日志
        log.info("文件上传:{}", file.toString());

        //原文件名
        String originalFilename = file.getOriginalFilename();

        //最后一个字符串"."出现的索引
        int index = originalFilename.lastIndexOf(".");

        //以最后一个"."出现的索引为索引切割出原文件名的文件类型
        String substring = originalFilename.substring(index);

        //使用uuid生成暂存文件名
        String fileName = UUID.randomUUID() + substring;

        //创建目标对象
        File dir = new File(basePath);

        //判断当前目录是否存在
        if (!dir.exists()) {
            //目标不存在,需要创建
            dir.mkdirs();
        }


        try {
            //将临时文件转存到指定位置
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);

    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {

        try {
            //输入流,通过输入流读取本地文件的文件内容
            FileInputStream fis = new FileInputStream(new File(basePath + name));

            //输出流,通过输出流把文件写会浏览器
            ServletOutputStream ops = response.getOutputStream();

            //指定响应文件格式
            response.setContentType("imag/jpeg");

            //使用commons-io 中的:
            //IOUtils.copy(InputStream input, OutputStream output)
            IOUtils.copy(fis, ops);

            //关闭资源
            fis.close();
            ops.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
