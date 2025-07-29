package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.edit.mapper.PromptSnippetMapper;
import cn.timflux.storyseek.core.write.edit.service.PromptSnippetEditService;
import cn.timflux.storyseek.core.write.promptsea.constant.PromptTagConstants;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptSnippetTagRelation;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptSnippetTagRelationMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: PromptSnippetServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:26
 * @Version 1.0
 */
@Slf4j
@Service
public class PromptSnippetEditServiceImpl implements PromptSnippetEditService {

    private final PromptSnippetMapper mapper;
    private final PromptSnippetTagRelationMapper tagRelationMapper;
    private final UserService userService;

    public PromptSnippetEditServiceImpl(PromptSnippetMapper mapper,
                                        UserService userService,
                                        PromptSnippetTagRelationMapper tagRelationMapper) {
        this.mapper = mapper;
        this.userService = userService;
        this.tagRelationMapper = tagRelationMapper;
    }

    /**
     * 添加提示词及标签关系
     */
    @Override
    @Transactional
    public Long addPromptSnippet(PromptSnippetDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();

        PromptSnippet snippet = new PromptSnippet();
        snippet.setUserId(currentUserId);
        snippet.setTitle(dto.getTitle());
        snippet.setSummary(dto.getSummary());
        snippet.setContent(dto.getContent());
        snippet.setPublished(dto.getPublished() != null && dto.getPublished());
        mapper.insert(snippet);

        saveTagRelations(snippet.getId(), dto.getTags(), snippet.getPublished());

        return snippet.getId();
    }

    /**
     * 更新提示词及标签关系
     */
    @Override
    @Transactional
    public void updatePromptSnippet(PromptSnippetDTO dto) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        PromptSnippet existing = mapper.selectById(dto.getId());

        if (existing == null || !existing.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限修改该提示词");
        }

        existing.setTitle(dto.getTitle());
        existing.setSummary(dto.getSummary());
        existing.setContent(dto.getContent());
        existing.setPublished(dto.getPublished());
        mapper.updateById(existing);

        // 更新标签关系
        tagRelationMapper.delete(new QueryWrapper<PromptSnippetTagRelation>()
                .eq("snippet_id", dto.getId()));
        log.info("dto.getTagIds():{}", dto.getTags());
        saveTagRelations(dto.getId(), dto.getTags(), existing.getPublished());
    }

    /**
     * 删除提示词及其关联标签
     */
    @Override
    @Transactional
    public void deletePromptSnippet(Long id) {
        Long currentUserId = StpUtil.getLoginIdAsLong();
        PromptSnippet snippet = mapper.selectById(id);

        if (snippet == null || !snippet.getUserId().equals(currentUserId)) {
            throw new RuntimeException("无权限删除该提示词");
        }

        tagRelationMapper.delete(new QueryWrapper<PromptSnippetTagRelation>()
                .eq("snippet_id", id));
        mapper.deleteById(id);
    }

    @Override
    public String getPromptText(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";

        Long userId = StpUtil.getLoginIdAsLong();
        String joinedIds = ids.stream().map(String::valueOf).collect(Collectors.joining(","));

        List<String> contents = mapper.getCollectedPromptContents(userId, joinedIds);
        return contents.stream().filter(Objects::nonNull).collect(Collectors.joining("\n\n"));
    }

    @Override
    public List<ListOptionDTO> getPromptOptions(Long userId) {
        return mapper.findOptionsByUserId(userId);
    }

    @Override
    public List<PromptSnippet> getDefaultSystemPromptSnippets() {
        QueryWrapper<PromptSnippet> wrapper = new QueryWrapper<>();
        wrapper.eq("published", true);
        wrapper.eq("is_default", true);
        return mapper.selectList(wrapper);
    }

    /**
     * 通用标签保存逻辑：更新或新增时使用
     * 若提示词已发布但未指定标签，则添加默认标签
     */
    private void saveTagRelations(Long snippetId, List<Long> tagIds, boolean isPublished) {
        if ((tagIds == null || tagIds.isEmpty()) && isPublished) {
            log.info("tagIds {},snippetId {}", tagIds, snippetId);
            tagIds = List.of(PromptTagConstants.DEFAULT_TAG_ID); // 添加默认“未分类”标签
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            log.info("ADD tagIds {},snippetId {}", tagIds, snippetId);
            List<PromptSnippetTagRelation> relations = tagIds.stream()
                    .map(tagId -> {
                        PromptSnippetTagRelation rel = new PromptSnippetTagRelation();
                        rel.setSnippetId(snippetId);
                        rel.setTagId(tagId);
                        return rel;
                    }).toList();
            log.info("relations {}", relations);
            relations.forEach(tagRelationMapper::insert);
        }
    }
}
