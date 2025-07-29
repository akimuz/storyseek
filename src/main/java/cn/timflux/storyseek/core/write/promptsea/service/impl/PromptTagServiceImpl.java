package cn.timflux.storyseek.core.write.promptsea.service.impl;

import cn.timflux.storyseek.core.write.promptsea.constant.PromptTagConstants;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagDTO;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptTag;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptTagCategory;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptTagCategoryMapper;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptTagMapper;
import cn.timflux.storyseek.core.write.promptsea.service.PromptTagService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: PromptTagServiceImpl
 * Package: cn.timflux.storyseek.core.write.promptsea.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午2:11
 * @Version 1.0
 */
@Service
public class PromptTagServiceImpl implements PromptTagService {

    private final PromptTagMapper tagMapper;
    private final PromptTagCategoryMapper tagCategoryMapper;

    public PromptTagServiceImpl(PromptTagMapper tagMapper, PromptTagCategoryMapper tagCategoryMapper) {
        this.tagMapper = tagMapper;
        this.tagCategoryMapper = tagCategoryMapper;
    }

    @Override
    public List<String> getTagNamesByIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return Collections.emptyList();

        List<PromptTag> tags = tagMapper.selectBatchIds(tagIds);
        return tags.stream()
                .map(PromptTag::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromptTagDTO> getAllTags() {
        List<PromptTag> tags = tagMapper.selectList(null);
        return tags.stream().map(tag -> {
            PromptTagDTO dto = new PromptTagDTO();
            dto.setId(tag.getId());
            dto.setName(tag.getName());
            dto.setCreatedAt(tag.getCreatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PromptTagDTO> getTagsByCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.isEmpty()) {
            return Collections.emptyList();
        }

        // 先根据 code 查询分类
        QueryWrapper<PromptTagCategory> wrapper = new QueryWrapper<>();
        wrapper.eq("code", categoryCode);
        PromptTagCategory category = tagCategoryMapper.selectOne(wrapper);

        if (category == null) {
            return Collections.emptyList();
        }

        // 查询标签
        QueryWrapper<PromptTag> tagWrapper = new QueryWrapper<>();
        tagWrapper.eq("category_id", category.getId());
        List<PromptTag> tags = tagMapper.selectList(tagWrapper);

        return tags.stream().map(tag -> {
            PromptTagDTO dto = new PromptTagDTO();
            dto.setId(tag.getId());
            dto.setName(tag.getName());
            dto.setCategoryId(tag.getCategoryId());
            return dto;
        }).collect(Collectors.toList());
    }


    @Override
    public Long createTag(String name) {
        PromptTag tag = new PromptTag();
        tag.setName(name);
        tag.setCategoryId(PromptTagConstants.DEFAULT_CATEGORY_ID);
        tagMapper.insert(tag);
        return tag.getId();
    }

    @Override
    public void deleteTag(Long tagId) {
        tagMapper.deleteById(tagId);
    }

    @PostConstruct
    @Override
    public void initDefaultTag() {
        PromptTag existing = tagMapper.selectById(PromptTagConstants.DEFAULT_TAG_ID);
        if (existing == null) {
            PromptTag defaultTag = new PromptTag();
            defaultTag.setId(PromptTagConstants.DEFAULT_TAG_ID);
            defaultTag.setName(PromptTagConstants.DEFAULT_TAG_NAME);
            defaultTag.setCategoryId(PromptTagConstants.DEFAULT_CATEGORY_ID);
            tagMapper.insert(defaultTag);
        }
    }
}
