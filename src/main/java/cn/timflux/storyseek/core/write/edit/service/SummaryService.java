package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.SummaryDTO;

import java.util.List;

/**
 * ClassName: SummaryService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:49
 * @Version 1.0
 */
public interface SummaryService {
    Long addSummary(SummaryDTO dto);
    void updateSummary(SummaryDTO dto);
    void deleteSummary(Long id);
    List<SummaryDTO> getSummariesByBook(Long bookId);
    List<ListOptionDTO> getSummaryOptions(Long bookId);
}
