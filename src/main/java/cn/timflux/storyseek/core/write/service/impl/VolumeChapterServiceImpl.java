package cn.timflux.storyseek.core.write.service.impl;
import cn.timflux.storyseek.core.write.dto.VolumeChapterTreeDTO;
import cn.timflux.storyseek.core.write.entity.VolumeChapter;
import cn.timflux.storyseek.core.write.mapper.VolumeChapterMapper;
import cn.timflux.storyseek.core.write.service.VolumeChapterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

/**
 * ClassName: VolumeChapterServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:08
 * @Version 1.0
 */
@Service
public class VolumeChapterServiceImpl implements VolumeChapterService {

    @Autowired
    private VolumeChapterMapper mapper;

    @Override
    public VolumeChapter createVolume(VolumeChapter volume) {
        volume.setType(1);
        volume.setParentId(0L);
        mapper.insert(volume);
        return volume;
    }

    @Override
    public VolumeChapter createChapter(VolumeChapter chapter) {
        chapter.setType(2);
        mapper.insert(chapter);
        return chapter;
    }

    @Override
    public List<VolumeChapterTreeDTO> getTreeDtoByBookId(Long bookId) {
        List<VolumeChapter> list = mapper.selectList(new QueryWrapper<VolumeChapter>()
                .eq("book_id", bookId)
                .orderByAsc("order_num"));

        Map<Long, VolumeChapterTreeDTO> map = new HashMap<>();
        List<VolumeChapterTreeDTO> roots = new ArrayList<>();

        for (VolumeChapter vc : list) {
            VolumeChapterTreeDTO dto = new VolumeChapterTreeDTO();
            dto.setId(vc.getId());
            dto.setBookId(vc.getBookId());
            dto.setParentId(vc.getParentId());
            dto.setName(vc.getName());
            dto.setType(vc.getType());
            dto.setOrderNum(vc.getOrderNum());
            map.put(dto.getId(), dto);
        }

        for (VolumeChapterTreeDTO dto : map.values()) {
            Long pid = dto.getParentId();
            if (pid == null || pid == 0) {
                roots.add(dto);
            } else {
                VolumeChapterTreeDTO parent = map.get(pid);
                if (parent != null) {
                    parent.getChildren().add(dto);
                } else {
                    roots.add(dto);
                }
            }
        }

        return roots;
    }

    @Override
    public boolean updateName(Long id, String newName) {
        VolumeChapter vc = new VolumeChapter();
        vc.setId(id);
        vc.setName(newName);
        return mapper.updateById(vc) > 0;
    }

    @Transactional
    @Override
    public boolean deleteById(Long id) {
        List<Long> toDelete = new ArrayList<>();
        toDelete.add(id);
        collectChildIds(id, toDelete);
        return mapper.deleteBatchIds(toDelete) == toDelete.size();
    }

    private void collectChildIds(Long parentId, List<Long> ids) {
        List<VolumeChapter> children = mapper.selectList(new QueryWrapper<VolumeChapter>()
                .eq("parent_id", parentId));
        for (VolumeChapter child : children) {
            ids.add(child.getId());
            collectChildIds(child.getId(), ids);
        }
    }
}