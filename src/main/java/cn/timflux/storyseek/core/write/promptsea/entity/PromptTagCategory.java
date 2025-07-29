package cn.timflux.storyseek.core.write.promptsea.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: PromptTagCategory
 * Package: cn.timflux.storyseek.core.write.promptsea.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:06
 * @Version 1.0
 */
@Data
@TableName("prompt_tag_category")
public class PromptTagCategory {
    private Long id;
    private String name;
    private String code;
    private LocalDateTime createdAt;
}
