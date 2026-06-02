package com.bookstore.controller;

import com.bookstore.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传接口
 */
@Tag(name = "文件上传", description = "图片上传")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Operation(summary = "上传图片")
    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 获取文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 确保上传目录存在
        File dir = new File(uploadPath).getAbsoluteFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        try {
            file.transferTo(new File(dir, fileName));
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }

        // 返回访问路径
        return Result.success("/uploads/" + fileName);
    }
}
