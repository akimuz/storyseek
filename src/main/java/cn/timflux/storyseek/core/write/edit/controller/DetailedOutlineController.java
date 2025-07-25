package cn.timflux.storyseek.core.write.edit.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.DetailedOutlineDTO;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.service.DetailedOutlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: DetailedOutlineController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/detailedOutline")
@Slf4j
public class DetailedOutlineController {

    @Autowired
    private DetailedOutlineService detailedOutlineService;

    @PostMapping("/saveOrUpdate")
    public ApiResponse<?> saveOrUpdate(@RequestBody DetailedOutlineDTO dto) {
        if (dto.getId() == null) {
            detailedOutlineService.saveDetailedOutline(dto);
            return ApiResponse.ok("新增成功");
        } else {
            detailedOutlineService.updateDetailedOutline(dto);
            return ApiResponse.ok("更新成功");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        detailedOutlineService.deleteDetailedOutline(id);
        return ApiResponse.ok("删除成功");
    }

    @GetMapping("/get")
    public ApiResponse<DetailedOutlineDTO> get(@RequestParam Long id) {
        DetailedOutlineDTO dto = detailedOutlineService.getDetailedOutlineById(id);
        return ApiResponse.ok(dto);
    }

    @GetMapping("/listByOutline")
    public ApiResponse<List<DetailedOutlineDTO>> listByOutline(@RequestParam Long outlineId) {
        List<DetailedOutlineDTO> list = detailedOutlineService.listByOutlineId(outlineId);
        return ApiResponse.ok(list);
    }

    @GetMapping(("/listByBook"))
    public ApiResponse<List<DetailedOutlineDTO>> listByBook(@RequestParam Long bookId) {
        List<DetailedOutlineDTO> list = detailedOutlineService.listByBookId(bookId);
        return ApiResponse.ok(list);
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long bookId) {
        List<ListOptionDTO> list = detailedOutlineService.getDetailedOutlineOptions(bookId);
        return ApiResponse.ok(list);
    }
}
