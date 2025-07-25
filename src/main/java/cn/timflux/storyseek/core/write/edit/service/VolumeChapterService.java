package cn.timflux.storyseek.core.write.edit.service;
import cn.timflux.storyseek.core.write.edit.dto.VolumeChapterTreeDTO;
import cn.timflux.storyseek.core.write.edit.entity.VolumeChapter;
import java.util.List;

/**
 * ClassName: VolumeChapterService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:07
 * @Version 1.0
 */
public interface VolumeChapterService {

    VolumeChapter createVolume(VolumeChapter volume);

    VolumeChapter createChapter(VolumeChapter chapter);

    /**
     * 获取指定书籍的卷章树形结构DTO
     */
    List<VolumeChapterTreeDTO> getTreeDtoByBookId(Long bookId);

    /**
     * 根据ID修改卷章名称
     */
    boolean updateName(Long id, String newName);

    /**
     * 根据ID删除卷或章，卷会级联删除其子章
     */
    boolean deleteById(Long id);
}
