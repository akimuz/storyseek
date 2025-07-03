package cn.timflux.storyseek.core.write.controller;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.dto.ChapterContentDTO;
import cn.timflux.storyseek.core.write.service.ChapterContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ClassName: ChapterContentController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:37
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/write/chapter-content")
public class ChapterContentController {

    @Autowired
    private ChapterContentService service;

    @GetMapping
    public ApiResponse<ChapterContentDTO> getContent(@RequestParam Long chapterId) {
        return ApiResponse.ok(service.getChapterContent(chapterId));
    }

    @PostMapping
    public ApiResponse<Void> saveContent(@RequestBody ChapterContentDTO dto) {
        service.saveChapterContent(dto);
        return ApiResponse.ok();
    }

    @PostMapping("/generate-summary")
    public ApiResponse<String> generateSmartSummary(@RequestParam Long chapterId) {
        String summary = service.generateSmartSummary(chapterId);
        return ApiResponse.ok(summary);
    }
}
