package cn.timflux.storyseek.core.write.service;

import cn.timflux.storyseek.core.write.dto.OutlineDTO;

import java.util.List;

/**
 * ClassName: OutlineService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午3:45
 * @Version 1.0
 */
public interface OutlineService {
    void saveOutline(OutlineDTO dto);
    void updateOutline(OutlineDTO dto);
    void deleteOutline(Long id);
    OutlineDTO getOutlineById(Long id);
    List<OutlineDTO> listByBookId(Long bookId);
}
