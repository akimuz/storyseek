package cn.timflux.storyseek.core.write.edit.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: PromptSnippet
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:46
 * @Version 1.0
 */
@Data
@TableName("prompt_snippet")
public class PromptSnippet {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String title;
    private String summary;
    private String content;
    private Long tagsId;
    private Boolean published; // 是否公开发布到提示词海
    private Long favoriteCount;
    private Boolean isDefault; // 是否新用户注册默认添加

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
