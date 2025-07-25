package cn.timflux.storyseek.core.write.edit.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.PromptSnippetDTO;
import cn.timflux.storyseek.core.write.edit.service.PromptSnippetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: PromptSnippetController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午2:06
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/prompt/snippet")
public class PromptSnippetController {

    private final PromptSnippetService promptSnippetService;

    public PromptSnippetController(PromptSnippetService promptSnippetService) {
        this.promptSnippetService = promptSnippetService;
    }

    @PostMapping("/add")
    public ApiResponse<Long> add(@RequestBody PromptSnippetDTO dto) {
        return ApiResponse.ok(promptSnippetService.addPromptSnippet(dto));
    }

    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody PromptSnippetDTO dto) {
        promptSnippetService.updatePromptSnippet(dto);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        promptSnippetService.deletePromptSnippet(id);
        return ApiResponse.ok();
    }

    // 探索页接口，返回简介信息，不包含内容
    @GetMapping("/explore")
    public ApiResponse<List<PromptSnippetDTO>> explore(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "created_at") String orderBy,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        Long currentUserId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        List<PromptSnippetDTO> snippets = promptSnippetService.getSnippetListSafe(keyword, orderBy, limit, offset, currentUserId);
        return ApiResponse.ok(snippets);
    }

    // 获取详情页接口，内容字段仅作者可见
    @GetMapping("/getById/{id}")
    public ApiResponse<PromptSnippetDTO> getSnippet(@PathVariable Long id) {
        Long currentUserId = StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        PromptSnippetDTO dto = promptSnippetService.getSnippetByIdSafe(id, currentUserId);
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
        return ApiResponse.ok(promptSnippetService.getSnippetByUser(userId));
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long userId) {
        return ApiResponse.ok(promptSnippetService.getPromptOptions(userId));
    }
}

