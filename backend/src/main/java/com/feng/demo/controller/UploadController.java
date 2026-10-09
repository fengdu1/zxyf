package com.feng.demo.controller;

import com.feng.demo.common.Result;
import com.feng.demo.service.EmpService;
import com.feng.demo.service.UploadService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件上传 / 下载接口
 * <p>
 * POST /upload：上传，multipart/form-data，参数名 file，返回文件访问 URL。
 * GET /download?url=文件URL&name=原始文件名：下载，按上传时的文件名返回。
 */
@RestController
@RequestMapping("/file")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private EmpService empService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        return Result.success(uploadService.upload(file));
    }

    @GetMapping("/download")
    public void download(@RequestParam("url") String url,
                         HttpServletResponse response) throws IOException {

        // 先去数据库查原始文件名（emp.image 存 OSS URL，original_name 存原始文件名）
        String name = empService.findOriginalNameByUrl(url);
        if (name == null || name.isEmpty()) {
            // 未找到记录时兜底：使用 URL 中的 UUID 文件名
            name = url.substring(url.lastIndexOf("/") + 1);
        }
        
        // 设置响应头
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 流式写入
        try (InputStream in = uploadService.downloadStream(url);
             OutputStream out = response.getOutputStream()) {
            in.transferTo(out);
        } catch (IOException e) {
            // log.error("文件下载失败, url: {}", url, e);
            throw e; 
        }
    }
}
