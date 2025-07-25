package cn.timflux.storyseek.core.write.edit.dto;

import lombok.Data;

/**
 * ClassName: ChapterContentDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:35
 * @Version 1.0
 */
@Data
public class ChapterContentDTO {
    private Long chapterId;
    private String content;
    private String draftSummary;      // 用户输入的梗概（自动保存用）
}
