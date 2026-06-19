package com.flowerstore.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {
    
    private Long total;
    private List<T> records;
    private Long current;
    private Long size;
    
    public PageResult(Long total, List<T> records, Long current, Long size) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
    }
    
    /**
     * 从 MyBatis-Plus 的 Page 对象转换为 PageResult
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getTotal(),
            page.getRecords(),
            page.getCurrent(),
            page.getSize()
        );
    }
}

