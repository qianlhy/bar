package com.flowerstore.service;

import com.flowerstore.config.FileUploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件服务
 */
@Service
public class FileService {

    @Autowired
    private FileUploadProperties uploadProperties;

    /**
     * 上传文件
     */
    public String upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }
        if (extension.isEmpty()) {
            extension = ".jpg";
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

        // 按日期创建子目录
        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File dir = new File(uploadProperties.getResolvedPath(), dateDir);
        if (!dir.exists() && !dir.mkdirs() && !dir.exists()) {
            throw new IOException("上传目录创建失败：" + dir.getAbsolutePath());
        }

        // transferTo 传入绝对路径，避免被解析到 Tomcat 临时工作目录
        File dest = new File(dir, newFilename).getAbsoluteFile();
        file.transferTo(dest);

        return uploadProperties.getDomain() + "/api/uploads/" + dateDir + "/" + newFilename;
    }

    /**
     * 删除文件
     */
    public void delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        // 去除域名和访问路径前缀，只保留 yyyy/MM/dd/xxx.jpg
        String relative = filePath;
        int index = relative.indexOf("/uploads/");
        if (index >= 0) {
            relative = relative.substring(index + "/uploads/".length());
        }
        if (relative.startsWith("/")) {
            relative = relative.substring(1);
        }

        File file = new File(uploadProperties.getResolvedPath(), relative);
        if (file.exists()) {
            file.delete();
        }
    }
}
