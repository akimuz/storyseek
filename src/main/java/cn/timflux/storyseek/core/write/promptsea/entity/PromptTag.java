package cn.timflux.storyseek.core.write.promptsea.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: PromptTag
 * Package: cn.timflux.storyseek.core.write.promptsea.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:42
 * @Version 1.0
 */
@Data
@TableName("prompt_tag")
public class PromptTag {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private Long categoryId;
    private LocalDateTime createdAt;
}