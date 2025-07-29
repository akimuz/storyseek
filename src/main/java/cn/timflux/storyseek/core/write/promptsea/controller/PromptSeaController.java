package cn.timflux.storyseek.core.write.promptsea.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.promptsea.service.PromptSeaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: PromptExploreController
 * Package: cn.timflux.storyseek.core.write.promptsea.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/27 上午12:02
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/prompt/sea")
public class PromptSeaController {
    private final PromptSeaService promptSeaService;
    public PromptSeaController(PromptSeaService promptSeaService){
        this.promptSeaService = promptSeaService;
    }
    // 探索页接口，返回简介信息，不包含内容
    @GetMapping("/explore")
    public ApiResponse<List<PromptSnippetDTO>> explore(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(defaultValue = "created_at") String orderBy,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        Long currentUserId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        List<PromptSnippetDTO> snippets = promptSeaService.explorePrompts(
            keyword, tagIds, limit, offset, orderBy, currentUserId
        );
        return ApiResponse.ok(snippets);
    }

    // 点击获取详情页，内容字段仅作者可见
    @GetMapping("/getById/{id}")
    public ApiResponse<PromptSnippetDTO> getSnippet(@PathVariable Long id) {
        Long currentUserId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        PromptSnippetDTO dto = promptSeaService.getSnippetByIdSafe(id, currentUserId);
        if (dto == null) {
            return ApiResponse.error("提示词不存在或未发布");
        }
        return ApiResponse.ok(dto);
    }

    @GetMapping("/listByUser")
    public ApiResponse<List<PromptSnippetDTO>> listByUser(@RequestParam Long userId){
        if (userId == null) {
            return ApiResponse.error("Missing or invalid userId");
        }
        Long currentUserId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        return ApiResponse.ok(promptSeaService.getSnippetByUser(userId, currentUserId));
    }
}
