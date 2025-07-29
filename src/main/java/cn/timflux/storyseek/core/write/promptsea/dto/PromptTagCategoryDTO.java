package cn.timflux.storyseek.core.write.promptsea.dto;

import lombok.Data;

import java.util.List;

/**
 * ClassName: PromptTagCategoryDTO
 * Package: cn.timflux.storyseek.core.write.promptsea.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:07
 * @Version 1.0
 */
@Data
public class PromptTagCategoryDTO {
    private Long id;
    private String name;
    private String code;
    private List<PromptTagDTO> tags;
}
