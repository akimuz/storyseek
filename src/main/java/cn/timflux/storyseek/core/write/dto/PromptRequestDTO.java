package cn.timflux.storyseek.core.write.dto;

import lombok.Data;

import java.util.List;

/**
 * ClassName: PromptRequest
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:16
 * @Version 1.0
 */
@Data
public class PromptRequestDTO {
    private String stage; // "role"/"world"/"outline" / "suboutline" / "text"
    private String content; // 用户输入的核心内容（故事核 / 大纲 / 细纲 / 正文要求）
    private List<Long> characterCardIds;
    private List<Long> worldSettingIds;
    private List<Long> promptSnippetIds; // 提示词条（用户保存或公共提示词）
    private List<Long> relatedChapterIds; // 关联的正文章节
    private List<Long> relatedSummaryIds; // 关联的梗概
    private String model; // 模型名称，如 gemini、openai
}
