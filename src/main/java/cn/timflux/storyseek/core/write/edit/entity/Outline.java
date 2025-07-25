package cn.timflux.storyseek.core.write.edit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: Outline
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午3:42
 * @Version 1.0
 */
@Data
@TableName("outline")
public class Outline {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId; // 对应 Book 表的主键
    private String content;
    private String title;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
