package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.timflux.storyseek.core.write.edit.dto.DetailedOutlineDTO;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.entity.DetailedOutline;
import cn.timflux.storyseek.core.write.edit.mapper.DetailedOutlineMapper;
import cn.timflux.storyseek.core.write.edit.service.DetailedOutlineService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: DetailedOutlineServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:30
 * @Version 1.0
 */
@Service
@Slf4j
public class DetailedOutlineServiceImpl implements DetailedOutlineService {

    @Autowired
    private DetailedOutlineMapper detailedOutlineMapper;

    @Override
    public void saveDetailedOutline(DetailedOutlineDTO dto) {
        DetailedOutline entity = new DetailedOutline();
        BeanUtils.copyProperties(dto, entity);
        int rows = detailedOutlineMapper.insert(entity);
        log.info("保存细纲，id: {}, 影响行数: {}", entity.getId(), rows);
    }

    @Override
    public void updateDetailedOutline(DetailedOutlineDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("更新时id不能为空");
        }
        DetailedOutline entity = new DetailedOutline();
        BeanUtils.copyProperties(dto, entity);
        int rows = detailedOutlineMapper.updateById(entity);
        log.info("更新细纲，id: {}, 影响行数: {}", dto.getId(), rows);
    }

    @Override
    public void deleteDetailedOutline(Long id) {
        int rows = detailedOutlineMapper.deleteById(id);
        log.info("删除细纲，id: {}, 影响行数: {}", id, rows);
    }

    @Override
    public DetailedOutlineDTO getDetailedOutlineById(Long id) {
        DetailedOutline entity = detailedOutlineMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        DetailedOutlineDTO dto = new DetailedOutlineDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<DetailedOutlineDTO> listByOutlineId(Long outlineId) {
        QueryWrapper<DetailedOutline> wrapper = new QueryWrapper<>();
        wrapper.eq("outline_id", outlineId);
        List<DetailedOutline> list = detailedOutlineMapper.selectList(wrapper);

        return list.stream().map(entity -> {
            DetailedOutlineDTO dto = new DetailedOutlineDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<DetailedOutlineDTO> listByBookId(Long book_id) {
        QueryWrapper<DetailedOutline> wrapper = new QueryWrapper<>();
        wrapper.eq("book_id", book_id);
        List<DetailedOutline> list = detailedOutlineMapper.selectList(wrapper);

        return list.stream().map(entity -> {
            DetailedOutlineDTO dto = new DetailedOutlineDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public String getPromptText(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";
        List<DetailedOutline> settings = detailedOutlineMapper.selectBatchIds(ids);
        return settings.stream()
                .map(s -> String.format("细纲：%s\n描述：%s", s.getTitle(), s.getContent()))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public List<ListOptionDTO> getDetailedOutlineOptions(Long bookId) {
        return detailedOutlineMapper.findOptionsByBookId(bookId);
    }

}
