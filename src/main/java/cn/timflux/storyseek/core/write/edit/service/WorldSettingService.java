package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.edit.dto.WorldSettingDTO;
import cn.timflux.storyseek.core.write.edit.entity.WorldSetting;

import java.util.List;

/**
 * ClassName: WorldSettingService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:00
 * @Version 1.0
 */

public interface WorldSettingService {
    List<WorldSetting> getByBookId(Long bookId);
    void create(WorldSettingDTO dto);
    void update(Long id, WorldSettingDTO dto);
    void delete(Long id);
    String getPromptText(List<Long> ids);
    List<ListOptionDTO> getWordOptions(Long bookId);
}
