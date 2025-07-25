package cn.timflux.storyseek.core.write.edit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: DetailedOutline
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午3:42
 * @Version 1.0
 */
@Data
@TableName("detailed_outline")
public class DetailedOutline {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private Long outlineId; // 对应 Outline 表主键
    private String content;
    private String title;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
