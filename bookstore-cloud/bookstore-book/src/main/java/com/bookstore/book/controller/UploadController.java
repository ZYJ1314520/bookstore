package com.bookstore.book.controller;

import com.bookstore.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("文件不能为空");

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID().toString() + extension;
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();

        File targetFile = new File(dir.getAbsolutePath(), filename);
        log.info("上传文件: {} -> {}", originalFilename, targetFile.getAbsolutePath());

        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.error("文件上传失败: " + e.getMessage());
        }

        return Result.success("/uploads/" + filename);
    }
}
