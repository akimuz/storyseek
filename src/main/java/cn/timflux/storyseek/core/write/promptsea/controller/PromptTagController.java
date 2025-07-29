package cn.timflux.storyseek.core.write.promptsea.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptTagDTO;
import cn.timflux.storyseek.core.write.promptsea.service.PromptTagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: PromptTagController
 * Package: cn.timflux.storyseek.core.write.promptsea.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 下午3:00
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/tags")
public class PromptTagController {

    private final PromptTagService promptTagService;

    public PromptTagController(PromptTagService promptTagService) {
        this.promptTagService = promptTagService;
    }

    /**
     * 获取全部标签列表（前端用于下拉选择或筛选）
     * GET /api/tags
     */
    @GetMapping
    public ApiResponse<List<PromptTagDTO>> getAllTags() {
        return ApiResponse.ok(promptTagService.getAllTags());
    }

    @GetMapping("/by-category")
    public ApiResponse<List<PromptTagDTO>> getTagsByCategory(@RequestParam String code) {
        return ApiResponse.ok(promptTagService.getTagsByCategoryCode(code));
    }
    /**
     * 创建新标签（可限制为管理员调用）
     * POST /api/tags
     */
    @PostMapping
    public ApiResponse<Long> createTag(@RequestParam String name) {
        return ApiResponse.ok(promptTagService.createTag(name));
    }

    /**
     * 删除标签（可限制为管理员调用）
     * DELETE /api/tags/{tagId}
     */
    @DeleteMapping("/{tagId}")
    public ApiResponse<Void> deleteTag(@PathVariable Long tagId) {
        promptTagService.deleteTag(tagId);
        return ApiResponse.ok();
    }
}

