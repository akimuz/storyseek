package cn.timflux.storyseek.core.write.promptsea.service;

import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagDTO;

import java.util.List;

/**
 * ClassName: PromptTagService
 * Package: cn.timflux.storyseek.core.write.promptsea.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:10
 * @Version 1.0
 */
public interface PromptTagService {
    List<String> getTagNamesByIds(List<Long> tagIds);
    List<PromptTagDTO> getAllTags();
    List<PromptTagDTO> getTagsByCategoryCode(String code);
    Long createTag(String name);
    void deleteTag(Long tagId);
    void initDefaultTag();
}
