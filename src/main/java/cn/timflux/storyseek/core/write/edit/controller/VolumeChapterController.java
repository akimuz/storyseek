package cn.timflux.storyseek.core.write.edit.controller;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.write.edit.entity.VolumeChapter;
import cn.timflux.storyseek.core.write.edit.service.VolumeChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import cn.timflux.storyseek.core.write.edit.dto.VolumeChapterCreateDTO;
import cn.timflux.storyseek.core.write.edit.dto.VolumeChapterRenameDTO;
import cn.timflux.storyseek.core.write.edit.dto.VolumeChapterTreeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * ClassName: VolumeChapterController
 * Package: cn.timflux.storyseek.core.write.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:09
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/write/volume-chapter")
public class VolumeChapterController {

    @Autowired
    private VolumeChapterService service;

    @PostMapping("/volume")
    public ApiResponse<VolumeChapter> createVolume(@RequestBody VolumeChapterCreateDTO dto) {
        VolumeChapter volume = new VolumeChapter();
        BeanUtils.copyProperties(dto, volume);
        volume.setParentId(0L);
        volume.setType(1);
        VolumeChapter created = service.createVolume(volume);
        return ApiResponse.ok(created);
    }

    @PostMapping("/chapter")
    public ApiResponse<VolumeChapter> createChapter(@RequestBody VolumeChapterCreateDTO dto) {
        VolumeChapter chapter = new VolumeChapter();
        BeanUtils.copyProperties(dto, chapter);
        chapter.setType(2);
        VolumeChapter created = service.createChapter(chapter);
        return ApiResponse.ok(created);
    }

    @GetMapping("/tree")
    public ApiResponse<List<VolumeChapterTreeDTO>> getTree(@RequestParam Long bookId) {
        List<VolumeChapterTreeDTO> tree = service.getTreeDtoByBookId(bookId);
        return ApiResponse.ok(tree);
    }

    @PutMapping("/rename")
    public ApiResponse<Void> rename(@RequestBody VolumeChapterRenameDTO dto) {
        boolean ok = service.updateName(dto.getId(), dto.getName());
        if (ok) return ApiResponse.ok();
        else return ApiResponse.error("更新失败");
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long id) {
        boolean ok = service.deleteById(id);
        if (ok) return ApiResponse.ok();
        else return ApiResponse.error("删除失败");
    }
}
