package cn.timflux.storyseek.core.write.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.dto.OutlineDTO;
import cn.timflux.storyseek.core.write.service.OutlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: OutlineController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:20
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/outline")
@Slf4j
public class OutlineController {

    @Autowired
    private OutlineService outlineService;

    @PostMapping("/saveOrUpdate")
    public ApiResponse<?> saveOrUpdate(@RequestBody OutlineDTO dto) {
        if (dto.getId() == null) {
            outlineService.saveOutline(dto);
            return ApiResponse.ok("新增成功");
        } else {
            outlineService.updateOutline(dto);
            return ApiResponse.ok("更新成功");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id) {
        outlineService.deleteOutline(id);
        return ApiResponse.ok("删除成功");
    }

    @GetMapping("/get")
    public ApiResponse<OutlineDTO> get(@RequestParam Long id) {
        OutlineDTO dto = outlineService.getOutlineById(id);
        return ApiResponse.ok(dto);
    }

    @GetMapping("/listByBook")
    public ApiResponse<List<OutlineDTO>> listByBook(@RequestParam Long bookId) {
        List<OutlineDTO> list = outlineService.listByBookId(bookId);
        return ApiResponse.ok(list);
    }
}

