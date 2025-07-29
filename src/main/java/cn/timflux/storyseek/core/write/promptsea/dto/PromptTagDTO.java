package cn.timflux.storyseek.core.write.promptsea.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: PromptTagDTO
 * Package: cn.timflux.storyseek.core.write.promptsea.dto
 * Description:
 * 标签展示
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午12:05
 * @Version 1.0
 */
@Data
public class PromptTagDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private LocalDateTime createdAt;
}
