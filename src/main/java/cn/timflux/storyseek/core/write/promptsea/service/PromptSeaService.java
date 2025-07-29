package cn.timflux.storyseek.core.write.promptsea.service;

import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;

import java.util.List;

/**
 * ClassName: PromptSeaService
 * Package: cn.timflux.storyseek.core.write.promptsea.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:31
 * @Version 1.0
 */

public interface PromptSeaService {

    /**
     * 获取提示词海中的提示词（带标签筛选、分页、关键词）
     */
    List<PromptSnippetDTO> explorePrompts(String keyword, List<Long> tagIds, int limit, int offset, String orderBy, Long excludeUserId);

    PromptSnippetDTO getSnippetByIdSafe(Long id, Long currentUserId);

    List<PromptSnippetDTO> getSnippetByUser(Long userId, Long currentUserId);

    List<PromptSnippet> getDefaultSystemPromptSnippets();
}