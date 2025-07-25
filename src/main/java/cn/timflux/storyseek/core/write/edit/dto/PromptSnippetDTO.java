package cn.timflux.storyseek.core.write.edit.dto;

import lombok.Data;

/**
 * ClassName: PromptSnippetDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:46
 * @Version 1.0
 */
@Data
public class PromptSnippetDTO {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private String content;  // 若非作者返回 null
    private String authorName;
    private Boolean published;
}