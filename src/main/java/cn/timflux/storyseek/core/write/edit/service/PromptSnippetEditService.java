package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;

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
public interface PromptSnippetEditService {
    Long addPromptSnippet(PromptSnippetDTO dto);
    void updatePromptSnippet(PromptSnippetDTO dto);
    void deletePromptSnippet(Long id);
    String getPromptText(List<Long> ids);
    List<ListOptionDTO> getPromptOptions(Long userId);
    List<PromptSnippet> getDefaultSystemPromptSnippets();
}
