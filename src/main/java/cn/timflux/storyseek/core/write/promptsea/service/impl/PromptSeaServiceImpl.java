package cn.timflux.storyseek.core.write.promptsea.service.impl;

import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.edit.mapper.PromptSnippetMapper;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptTag;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptSnippetTagRelationMapper;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptTagMapper;
import cn.timflux.storyseek.core.write.promptsea.service.PromptSeaService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: PromptSeaServiceImpl
 * Package: cn.timflux.storyseek.core.write.promptsea.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:34
 * @Version 1.0
 */
@Service
public class PromptSeaServiceImpl implements PromptSeaService {

    private final PromptSnippetMapper snippetMapper;
    private final PromptSnippetTagRelationMapper relationMapper;
    private final PromptTagMapper tagMapper;
    private final UserService userService;

    public PromptSeaServiceImpl(PromptSnippetMapper snippetMapper,
                                PromptSnippetTagRelationMapper relationMapper,
                                PromptTagMapper tagMapper,
                                UserService userService) {
        this.snippetMapper = snippetMapper;
        this.relationMapper = relationMapper;
        this.tagMapper = tagMapper;
        this.userService = userService;
    }

    @Override
    public List<PromptSnippetDTO> explorePrompts(String keyword, List<Long> tagIds, int limit, int offset, String orderBy, Long excludeUserId) {
        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.eq("published", true);

        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like("title", keyword);
        }

        Set<String> allowedOrderFields = Set.of("created_at", "updated_at", "title");
        if (allowedOrderFields.contains(orderBy)) {
            wrapper.orderByDesc(orderBy);
        } else {
            wrapper.orderByDesc("created_at");
        }

        // 传入 tagId 时筛选中间表
        if (tagIds != null && !tagIds.isEmpty()) {
            // This requires a new method in your relationMapper
            List<Long> snippetIds = relationMapper.findSnippetIdsByTagIds(tagIds);
            if (snippetIds.isEmpty()) {
                return List.of();
            }
            wrapper.in("id", snippetIds);
        }

        int pageNum = offset / limit + 1;
        Page<PromptSnippet> page = new Page<>(pageNum, limit);
        List<PromptSnippet> snippets = snippetMapper.selectPage(page, wrapper).getRecords();

        return snippets.stream()
                .map(snippet -> toDTO(snippet, null)) // 探索页不看 content
                .collect(Collectors.toList());
    }

    @Override
    public PromptSnippetDTO getSnippetByIdSafe(Long id, Long currentUserId) {
        PromptSnippet snippet = snippetMapper.selectById(id);
        if (snippet == null) {
            return null;
        }
        return toDTO(snippet, currentUserId);
    }

    @Override
    public List<PromptSnippetDTO> getSnippetByUser(Long targetUserId, Long currentUserId) {
        boolean isOwner = targetUserId.equals(currentUserId);
        List<PromptSnippet> list;

        if (isOwner) {
            list = snippetMapper.findAllByUserId(targetUserId); // 包含全部
        } else {
            list = snippetMapper.findPublishedByUserId(targetUserId); // 只看发布
        }

        return list.stream()
            .map(snippet -> toDTO(snippet, null))
            .collect(Collectors.toList());
    }


    @Override
    public List<PromptSnippet> getDefaultSystemPromptSnippets() {
        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.eq("published", true);
        wrapper.eq("is_default", true);
        return snippetMapper.selectList(wrapper);
    }

    private PromptSnippetDTO toDTO(PromptSnippet snippet, Long currentUserId) {
        PromptSnippetDTO dto = new PromptSnippetDTO();
        dto.setId(snippet.getId());
        dto.setUserId(snippet.getUserId());
        dto.setTitle(snippet.getTitle());
        dto.setSummary(snippet.getSummary());
        dto.setPublished(snippet.getPublished());

        // 用户名
        User user = userService.getById(snippet.getUserId());
        dto.setAuthorName(user != null ? user.getUsername() : "未知用户");

        // 是否展示内容
        if (currentUserId != null && currentUserId.equals(snippet.getUserId())) {
            dto.setContent(snippet.getContent());
        } else {
            dto.setContent(null); // 非本人则隐藏内容
        }
        // 查询标签名列表
        List<Long> tagIds = relationMapper.findTagIdsBySnippetId(snippet.getId());
        if (!tagIds.isEmpty()) {
            List<PromptTag> tags = tagMapper.selectBatchIds(tagIds);
            List<String> tagNames = tags.stream().map(PromptTag::getName).collect(Collectors.toList());
            dto.setTagNames(tagNames);
        } else {
            dto.setTagNames(List.of());
        }

        return dto;
    }
}
