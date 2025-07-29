package cn.timflux.storyseek.core.write.promptsea.service.impl;

import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagCategoryDTO;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagDTO;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptTag;
import cn.timflux.storyseek.core.write.promptsea.entity.PromptTagCategory;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptTagCategoryMapper;
import cn.timflux.storyseek.core.write.promptsea.mapper.PromptTagMapper;
import cn.timflux.storyseek.core.write.promptsea.service.PromptTagCategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ClassName: PromptTagCategoryServiceImpl
 * Package: cn.timflux.storyseek.core.write.promptsea.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:11
 * @Version 1.0
 */
@Service
public class PromptTagCategoryServiceImpl implements PromptTagCategoryService {

    private final PromptTagCategoryMapper categoryMapper;
    private final PromptTagMapper tagMapper;

    public PromptTagCategoryServiceImpl(PromptTagCategoryMapper categoryMapper,
                                        PromptTagMapper tagMapper) {
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
    }

    /**
     * 返回所有分类及每个分类下的标签列表
     */
    @Override
    public List<PromptTagCategoryDTO> getAllCategoriesWithTags() {
        List<PromptTagCategory> categories = categoryMapper.selectList(null);
        List<PromptTag> tags = tagMapper.selectList(null);

        Map<Long, List<PromptTagDTO>> tagMap = tags.stream()
                .map(this::toDTO)
                .collect(Collectors.groupingBy(PromptTagDTO::getCategoryId));

        return categories.stream().map(category -> {
            PromptTagCategoryDTO dto = new PromptTagCategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setCode(category.getCode());
            dto.setTags(tagMap.getOrDefault(category.getId(), new ArrayList<>()));
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 仅返回分类基本信息（不带标签）
     */
    @Override
    public List<PromptTagCategoryDTO> getAllCategories() {
        List<PromptTagCategory> categories = categoryMapper.selectList(null);
        return categories.stream().map(category -> {
            PromptTagCategoryDTO dto = new PromptTagCategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setCode(category.getCode());
            dto.setTags(null);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 初始化默认分类（仅启动时运行）
     */
    @PostConstruct
    @Override
    public void createDefaultCategoriesIfNotExist() {
        List<String> defaultCodes = List.of("stage", "genre", "goal");
        Map<String, String> defaultNames = Map.of(
            "stage", "写作阶段",
            "genre", "题材类型",
            "goal", "其他"
        );

        for (String code : defaultCodes) {
            QueryWrapper<PromptTagCategory> wrapper = new QueryWrapper<>();
            wrapper.eq("code", code);
            PromptTagCategory existing = categoryMapper.selectOne(wrapper);
            if (existing == null) {
                PromptTagCategory category = new PromptTagCategory();
                category.setName(defaultNames.getOrDefault(code, code));
                category.setCode(code);
                categoryMapper.insert(category);
            }
        }
    }

    private PromptTagDTO toDTO(PromptTag tag) {
        PromptTagDTO dto = new PromptTagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setCategoryId(tag.getCategoryId());
        return dto;
    }
}
