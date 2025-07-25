package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.SummaryDTO;
import cn.timflux.storyseek.core.write.edit.entity.Summary;
import cn.timflux.storyseek.core.write.edit.mapper.SummaryMapper;
import cn.timflux.storyseek.core.write.edit.service.SummaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: SummaryServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:49
 * @Version 1.0
 */
@Slf4j
@Service
public class SummaryServiceImpl implements SummaryService {

    private final SummaryMapper summaryMapper;

    public SummaryServiceImpl(SummaryMapper summaryMapper) {
        this.summaryMapper = summaryMapper;
    }

    @Override
    public Long addSummary(SummaryDTO dto) {
        Summary summary = new Summary();
        BeanUtils.copyProperties(dto, summary);
        summaryMapper.insert(summary);
        log.info("新增摘要：{}", summary.getId());
        return summary.getId();
    }

    @Override
    public void updateSummary(SummaryDTO dto) {
        Summary summary = new Summary();
        BeanUtils.copyProperties(dto, summary);
        summaryMapper.update(summary);
        log.info("更新摘要：{}", summary.getId());
    }

    @Override
    public void deleteSummary(Long id) {
        summaryMapper.deleteById(id);
        log.info("删除摘要：{}", id);
    }

    @Override
    public List<SummaryDTO> getSummariesByBook(Long bookId) {
        return summaryMapper.findByBookId(bookId).stream().map(summary -> {
            SummaryDTO dto = new SummaryDTO();
            BeanUtils.copyProperties(summary, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ListOptionDTO> getSummaryOptions(Long bookId) {
        return summaryMapper.findOptionsByBookId(bookId);
    }
}
