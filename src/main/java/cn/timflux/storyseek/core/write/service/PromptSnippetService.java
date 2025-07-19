package cn.timflux.storyseek.core.write.service;

import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.entity.PromptSnippet;

import java.util.List;

/**
 * ClassName: PromptSnippetService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:25
 * @Version 1.0
 */
public interface PromptSnippetService {
    Long addPromptSnippet(PromptSnippetDTO dto);
    void updatePromptSnippet(PromptSnippetDTO dto);
    void deletePromptSnippet(Long id);
    String getPromptText(List<Long> ids);
    List<PromptSnippetDTO> getSnippetListSafe(String keyword, String orderBy, int limit, int offset, Long currentUserId);
    PromptSnippetDTO getSnippetByIdSafe(Long id, Long currentUserId);
    List<PromptSnippetDTO> getSnippetByUser(Long userId);
    List<ListOptionDTO> getPromptOptions(Long bookId);
    List<PromptSnippet> getDefaultSystemPromptSnippets();
}
