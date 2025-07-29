package cn.timflux.storyseek.core.write.promptsea.service;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;

import java.util.List;

/**
 * ClassName: PromptSnippetFavoriteService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:07
 * @Version 1.0
 */
public interface PromptSnippetFavoriteService {
    void setFavorite(Long userId, Long snippetId, boolean favorite);

    List<PromptSnippet> getUserFavorites(Long userId, int page, int size);

    boolean isFavorite(Long userId, Long snippetId);

    List<ListOptionDTO> getFavorPromptOptions(Long userId);
}
