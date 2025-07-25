package cn.timflux.storyseek.core.write.edit.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.SummaryDTO;
import cn.timflux.storyseek.core.write.edit.service.SummaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: SummaryController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody SummaryDTO dto) {
        return ApiResponse.ok(summaryService.addSummary(dto));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody SummaryDTO dto) {
        dto.setId(id);
        summaryService.updateSummary(dto);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        summaryService.deleteSummary(id);
        return ApiResponse.ok();
    }

    @GetMapping
    public ApiResponse<List<SummaryDTO>> list(@RequestParam Long bookId) {
        return ApiResponse.ok(summaryService.getSummariesByBook(bookId));
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long bookId) {
        return ApiResponse.ok(summaryService.getSummaryOptions(bookId));
    }
}

