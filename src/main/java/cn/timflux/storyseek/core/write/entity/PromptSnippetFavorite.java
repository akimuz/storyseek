package cn.timflux.storyseek.core.write.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: PromptSnippetFavorite
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:10
 * @Version 1.0
 */
@Data
@TableName("prompt_snippet_favorite")
public class PromptSnippetFavorite {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Long userId; // 收藏者
    private Long snippetId; // 被收藏提示词卡的ID
    private String authorName;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
