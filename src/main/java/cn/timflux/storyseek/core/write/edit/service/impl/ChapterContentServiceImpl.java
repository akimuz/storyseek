package cn.timflux.storyseek.core.write.edit.service.impl;

import cn.timflux.storyseek.ai.model.ChatModel;
import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.ai.service.ChatModelFactory;
import cn.timflux.storyseek.core.write.edit.dto.ChapterContentDTO;
import cn.timflux.storyseek.core.write.edit.entity.VolumeChapter;
import cn.timflux.storyseek.core.write.edit.mapper.VolumeChapterMapper;
import cn.timflux.storyseek.core.write.edit.service.ChapterContentService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: ChapterContentServiceImpl
 * Package: cn.timflux.storyseek.core.write.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:36
 * @Version 1.0
 */
@Service
public class ChapterContentServiceImpl implements ChapterContentService {

    @Autowired
    private VolumeChapterMapper mapper;
    @Autowired
    private ChatModelFactory chatModelFactory;

    @Override
    public ChapterContentDTO getChapterContent(Long chapterId) {
        VolumeChapter chapter = mapper.selectById(chapterId);
        if (chapter == null || chapter.getType() != 2) {
            throw new IllegalArgumentException("章节不存在");
        }
        ChapterContentDTO dto = new ChapterContentDTO();
        dto.setChapterId(chapterId);
        dto.setContent(chapter.getContent());
        return dto;
    }

    @Override
    public void saveChapterContent(ChapterContentDTO dto) {
        VolumeChapter chapter = new VolumeChapter();
        chapter.setId(dto.getChapterId());
        chapter.setContent(dto.getContent());
        chapter.setDraftSummary(dto.getDraftSummary()); // 存入简易梗概
        mapper.updateById(chapter);
    }

    @Override
    public String generateSmartSummary(Long chapterId) {
        VolumeChapter chapter = mapper.selectById(chapterId);
        if (chapter == null || StringUtils.isBlank(chapter.getContent())) {
            throw new IllegalArgumentException("章节内容为空");
        }

        List<ChatMessage> messages = List.of(
            new ChatMessage("system", "你是一位小说助手，请根据给定的章节正文，总结出一句话概括本章内容。"),
            new ChatMessage("user", chapter.getContent())
        );

        ChatModel model = chatModelFactory.getModel("gemini");
        String summary = model.chat(messages);

        // 更新数据库中的智能梗概字段
        chapter.setSummary(summary);
        mapper.updateById(chapter);

        return summary;
    }
}
