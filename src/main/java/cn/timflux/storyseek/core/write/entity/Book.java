package cn.timflux.storyseek.core.write.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: Book
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:36
 * @Version 1.0
 */
@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String type;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

}