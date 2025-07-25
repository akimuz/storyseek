package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.DetailedOutlineDTO;
import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;

import java.util.List;

/**
 * ClassName: DetailedOutlineService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:29
 * @Version 1.0
 */
public interface DetailedOutlineService {
    void saveDetailedOutline(DetailedOutlineDTO dto);
    void updateDetailedOutline(DetailedOutlineDTO dto);
    void deleteDetailedOutline(Long id);
    DetailedOutlineDTO getDetailedOutlineById(Long id);
    List<DetailedOutlineDTO> listByOutlineId(Long outlineId);

    List<DetailedOutlineDTO> listByBookId(Long book_id);

    String getPromptText(List<Long> ids);

    List<ListOptionDTO> getDetailedOutlineOptions(Long bookId);
}