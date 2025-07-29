package cn.timflux.storyseek.core.write.promptsea.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagCategoryDTO;
import cn.timflux.storyseek.core.write.promptsea.service.PromptTagCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClassName: PromptTagCategoryController
 * Package: cn.timflux.storyseek.core.write.promptsea.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/categories")
public class PromptTagCategoryController {

    private final PromptTagCategoryService categoryService;

    public PromptTagCategoryController(PromptTagCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<PromptTagCategoryDTO>> getAllCategoriesWithTags() {
        return ApiResponse.ok(categoryService.getAllCategoriesWithTags());
    }
}

