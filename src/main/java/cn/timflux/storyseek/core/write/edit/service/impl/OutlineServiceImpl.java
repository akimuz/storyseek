package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.OutlineDTO;
import cn.timflux.storyseek.core.write.edit.entity.Outline;
import cn.timflux.storyseek.core.write.edit.mapper.OutlineMapper;
import cn.timflux.storyseek.core.write.edit.service.OutlineService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: OutlineServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:29
 * @Version 1.0
 */
@Service
@Slf4j
public class OutlineServiceImpl implements OutlineService {

    @Autowired
    private OutlineMapper outlineMapper; // MyBatis-Plus Mapper

    @Override
    public void saveOutline(OutlineDTO dto) {
        Outline entity = new Outline();
        BeanUtils.copyProperties(dto, entity);
        // id为null则自动插入
        int rows = outlineMapper.insert(entity);
        log.info("保存大纲，id: {}, 影响行数: {}", entity.getId(), rows);
    }

    @Override
    public void updateOutline(OutlineDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("更新时id不能为空");
        }
        Outline entity = new Outline();
        BeanUtils.copyProperties(dto, entity);
        int rows = outlineMapper.updateById(entity);
        log.info("更新大纲，id: {}, 影响行数: {}", dto.getId(), rows);
    }

    @Override
    public void deleteOutline(Long id) {
        int rows = outlineMapper.deleteById(id);
        log.info("删除大纲，id: {}, 影响行数: {}", id, rows);
    }

    @Override
    public OutlineDTO getOutlineById(Long id) {
        Outline entity = outlineMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        OutlineDTO dto = new OutlineDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<OutlineDTO> listByBookId(Long bookId) {
        QueryWrapper<Outline> wrapper = new QueryWrapper<>();
        wrapper.eq("book_id", bookId);
        List<Outline> list = outlineMapper.selectList(wrapper);

        return list.stream().map(entity -> {
            OutlineDTO dto = new OutlineDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public String getPromptText(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";
        List<Outline> settings = outlineMapper.selectBatchIds(ids);
        return settings.stream()
                .map(s -> String.format("大纲：%s\n描述：%s", s.getTitle(), s.getContent()))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public List<ListOptionDTO> getOutlineOptions(Long bookId) {
        return outlineMapper.findOptionsByBookId(bookId);
    }
}
