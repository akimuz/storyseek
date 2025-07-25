package cn.timflux.storyseek.core.write.edit.controller;

import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.WorldSettingDTO;
import cn.timflux.storyseek.core.write.edit.entity.WorldSetting;
import cn.timflux.storyseek.core.write.edit.service.WorldSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * ClassName: SettingController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:57
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/worlds")
public class SettingController {

    @Autowired
    private WorldSettingService worldSettingService;

    @GetMapping
    public ApiResponse<List<WorldSetting>> list(@RequestParam Long bookId) {
        return ApiResponse.ok(worldSettingService.getByBookId(bookId));
    }

    @PostMapping
    public ApiResponse<Void> add(@RequestBody WorldSettingDTO dto) {
        System.out.println("contr"+dto.getDescription());
        worldSettingService.create(dto);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody WorldSettingDTO dto) {
        worldSettingService.update(id, dto);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        worldSettingService.delete(id);
        return ApiResponse.ok();
    }

    @GetMapping("/optionlist")
    public ApiResponse<List<ListOptionDTO>> optionList(@RequestParam Long bookId) {
        return ApiResponse.ok(worldSettingService.getWordOptions(bookId));
    }
}