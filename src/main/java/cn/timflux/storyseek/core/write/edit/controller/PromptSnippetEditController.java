package cn.timflux.storyseek.core.write.edit.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.service.PromptSnippetEditService;
import cn.timflux.storyseek.core.write.promptsea.dto.PromptSnippetDTO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/api/prompt/snippet")
public class PromptSnippetEditController {

    private final PromptSnippetEditService promptSnippetEditService;

    public PromptSnippetEditController(PromptSnippetEditService promptSnippetEditService) {
        this.promptSnippetEditService = promptSnippetEditService;
    }

    @PostMapping("/add")
    public ApiResponse<Long> add(@RequestBody PromptSnippetDTO dto) {
        return ApiResponse.ok(promptSnippetEditService.addPromptSnippet(dto));
    }

    @PutMapping("/update")
    public ApiResponse<Void> update(@RequestBody PromptSnippetDTO dto) {
        promptSnippetEditService.updatePromptSnippet(dto);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        promptSnippetEditService.deletePromptSnippet(id);
        return ApiResponse.ok();
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long userId) {
        return ApiResponse.ok(promptSnippetEditService.getPromptOptions(userId));
    }
}

