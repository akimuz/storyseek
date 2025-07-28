package cn.timflux.storyseek.core.write.promptsea.service.impl;

import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.mapper.UserMapper;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippetFavorite;
import cn.timflux.storyseek.core.write.edit.mapper.PromptSnippetFavoriteMapper;
import cn.timflux.storyseek.core.write.edit.mapper.PromptSnippetMapper;
import cn.timflux.storyseek.core.write.promptsea.service.PromptSnippetFavoriteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * ClassName: PromptSnippetFavoriteServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:07
 * @Version 1.0
 */
@Slf4j
@Service
public class PromptSnippetFavoriteServiceImpl implements PromptSnippetFavoriteService {

    private final PromptSnippetFavoriteMapper favoriteMapper;
    private final PromptSnippetMapper snippetMapper;
    private final UserMapper userMapper;

    public PromptSnippetFavoriteServiceImpl(PromptSnippetFavoriteMapper favoriteMapper,
                                            PromptSnippetMapper snippetMapper, UserMapper userMapper) {
        this.favoriteMapper = favoriteMapper;
        this.snippetMapper = snippetMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void setFavorite(Long userId, Long snippetId, boolean favorite) {
        // 获取提示词信息
        PromptSnippet snippet = snippetMapper.selectById(snippetId);
        if (snippet == null) {
            throw new IllegalArgumentException("提示词不存在");
        }

        // 收藏逻辑
        if (favorite) {
            boolean exists = favoriteMapper.exists(
                new LambdaQueryWrapper<PromptSnippetFavorite>()
                    .eq(PromptSnippetFavorite::getUserId, userId)
                    .eq(PromptSnippetFavorite::getSnippetId, snippetId)
            );

            if (!exists) {
                // 查询作者用户名（冗余保存）
                User author = userMapper.selectById(snippet.getUserId());
                String authorName = author != null ? author.getUsername() : "佚名";

                PromptSnippetFavorite fav = new PromptSnippetFavorite();
                fav.setUserId(userId);
                fav.setSnippetId(snippetId);
                fav.setTitle(snippet.getTitle());
                fav.setAuthorName(authorName);
                fav.setCreatedAt(LocalDateTime.now());

                favoriteMapper.insert(fav);
                snippetMapper.updateFavoriteCount(snippetId, 1);

                log.info("用户 {} 收藏了提示词 {}（{}）", userId, snippetId, snippet.getTitle());
            }
        } else {
            int deleted = favoriteMapper.delete(
                new LambdaQueryWrapper<PromptSnippetFavorite>()
                    .eq(PromptSnippetFavorite::getUserId, userId)
                    .eq(PromptSnippetFavorite::getSnippetId, snippetId)
            );
            if (deleted > 0) {
                snippetMapper.updateFavoriteCount(snippetId, -1);
                log.info("用户 {} 取消收藏提示词 {}（{}）", userId, snippetId, snippet.getTitle());
            }
        }
    }


    @Override
    public List<PromptSnippet> getUserFavorites(Long userId, int page, int size) {
        List<Long> snippetIds = favoriteMapper.selectSnippetIdsByUserId(userId);
        if (snippetIds.isEmpty()) return Collections.emptyList();

        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.in("id", snippetIds);
        wrapper.orderByDesc("created_at");

        Page<PromptSnippet> pageParam = new Page<>(page, size);
        IPage<PromptSnippet> resultPage = snippetMapper.selectPage(pageParam, wrapper);
        return resultPage.getRecords();
    }

    @Override
    public boolean isFavorite(Long userId, Long snippetId) {
        Long count = favoriteMapper.selectCount(new QueryWrapper<PromptSnippetFavorite>()
                .eq("user_id", userId)
                .eq("snippet_id", snippetId));
        return count > 0;
    }

    @Override
    public List<ListOptionDTO> getFavorPromptOptions(Long userId) {
        return favoriteMapper.findOptionsByUserId(userId);
    }
}
