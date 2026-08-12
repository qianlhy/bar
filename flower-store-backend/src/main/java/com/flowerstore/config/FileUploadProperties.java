package com.flowerstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件上传配置
 *
 * 配置的 path 为相对路径时不能依赖 JVM 工作目录：内嵌 Tomcat 下 MultipartFile.transferTo 会把相对
 * 路径解析到 /tmp/tomcat.xxx/work 临时目录，导致 FileNotFoundException。这里统一解析成绝对路径
 * （相对路径以 jar 所在目录为基准，可用环境变量 APP_HOME 覆盖）。
 */
@Component
@ConfigurationProperties(prefix = "file.upload")
public class FileUploadProperties {

    private static final Logger log = LoggerFactory.getLogger(FileUploadProperties.class);

    private String path = "./uploads/";

    private String staticPattern = "/uploads/**";

    private long maxSize = 10485760L;

    private String domain;

    /** 解析后的绝对目录，以分隔符结尾 */
    private String resolvedPath;

    @PostConstruct
    public void init() {
        Path resolved = resolveConfiguredPath();
        if (!prepareDirectory(resolved)) {
            // 目录不可用时退回到用户目录，避免因为一个上传目录导致整个应用起不来
            Path fallback = Paths.get(System.getProperty("user.home"), "flower-store", "uploads")
                    .toAbsolutePath().normalize();
            log.warn("上传目录不可用：{}，回退到 {}", resolved, fallback);
            resolved = fallback;
            prepareDirectory(resolved);
        }
        String result = resolved.toString();
        this.resolvedPath = result.endsWith(File.separator) ? result : result + File.separator;
        log.info("文件上传目录：{}", this.resolvedPath);
    }

    private Path resolveConfiguredPath() {
        String configured = path == null ? "" : path.trim();
        if (configured.isEmpty()) {
            configured = "./uploads/";
        }
        // 兼容误配成 Windows 盘符路径的情况（如 E:\xxx），在 Linux 上按相对目录处理
        if (!isWindows() && configured.matches("^[A-Za-z]:[\\\\/].*")) {
            configured = "./uploads/";
        }
        configured = configured.replace('\\', File.separatorChar);

        Path candidate = Paths.get(configured);
        if (candidate.isAbsolute()) {
            return candidate.normalize();
        }
        return Paths.get(resolveHome(), configured).toAbsolutePath().normalize();
    }

    private boolean prepareDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
            return Files.isWritable(dir);
        } catch (IOException e) {
            return false;
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    private String resolveHome() {
        String home = System.getenv("APP_HOME");
        if (home == null || home.trim().isEmpty()) {
            home = System.getProperty("APP_HOME");
        }
        if (home != null && !home.trim().isEmpty()) {
            return home.trim();
        }
        try {
            File location = new File(FileUploadProperties.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            return location.isFile() ? location.getParent() : location.getAbsolutePath();
        } catch (Exception e) {
            return System.getProperty("user.dir");
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStaticPattern() {
        return staticPattern;
    }

    public void setStaticPattern(String staticPattern) {
        this.staticPattern = staticPattern;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getResolvedPath() {
        return resolvedPath;
    }
}
