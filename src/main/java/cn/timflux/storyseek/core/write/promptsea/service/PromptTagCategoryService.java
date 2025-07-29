package cn.timflux.storyseek.core.write.promptsea.service;

import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagCategoryDTO;

import java.util.List;

/**
 * ClassName: PromptTagCategoryService
 * Package: cn.timflux.storyseek.core.write.promptsea.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:08
 * @Version 1.0
 */
public interface PromptTagCategoryService {
    List<PromptTagCategoryDTO> getAllCategoriesWithTags();
    List<PromptTagCategoryDTO> getAllCategories();
    void createDefaultCategoriesIfNotExist(); // 用于初始化 system, stage 等
}