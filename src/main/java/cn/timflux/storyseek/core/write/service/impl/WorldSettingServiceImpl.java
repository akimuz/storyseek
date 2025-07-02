package cn.timflux.storyseek.core.write.service.impl;

import cn.timflux.storyseek.core.write.dto.ListOptionDTO;
import cn.timflux.storyseek.core.write.dto.WorldSettingDTO;
import cn.timflux.storyseek.core.write.entity.WorldSetting;
import cn.timflux.storyseek.core.write.mapper.WorldSettingMapper;
import cn.timflux.storyseek.core.write.service.WorldSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: WorldSettingServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:00
 * @Version 1.0
 */
@Slf4j
@Service
public class WorldSettingServiceImpl implements WorldSettingService {

    @Autowired
    private WorldSettingMapper mapper;

    @Override
    public List<WorldSetting> getByBookId(Long bookId) {
        return mapper.findByBookId(bookId);
    }

    @Override
    public void create(WorldSettingDTO dto) {
        WorldSetting setting = new WorldSetting();
        setting.setBookId(dto.getBookId());
        setting.setName(dto.getName());
        setting.setDescription(dto.getDescription());
        mapper.insert(setting);
        log.info("新增世界设定：{}", dto.getName());
    }

    @Override
    public void update(Long id, WorldSettingDTO dto) {
        WorldSetting setting = new WorldSetting();
        setting.setId(id);
        setting.setName(dto.getName());
        setting.setDescription(dto.getDescription());
        mapper.update(setting);
        log.info("更新世界设定：{}", id);
    }

    @Override
    public void delete(Long id) {
        mapper.delete(id);
        log.info("删除世界设定：{}", id);
    }

    @Override
    public String getPromptText(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return "";
        List<WorldSetting> settings = mapper.selectBatchIds(ids);
        return settings.stream()
                .map(s -> String.format("设定：%s\n描述：%s", s.getName(), s.getDescription()))
                .collect(Collectors.joining("\n\n"));
    }

    @Override
    public List<ListOptionDTO> getWordOptions(Long bookId) {
        return mapper.findOptionsByBookId(bookId);
    }
}