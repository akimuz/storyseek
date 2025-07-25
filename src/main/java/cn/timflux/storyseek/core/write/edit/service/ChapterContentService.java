package cn.timflux.storyseek.core.write.edit.service;

import cn.timflux.storyseek.core.write.edit.dto.ChapterContentDTO;

/**
 * ClassName: ChapterContentService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午4:36
 * @Version 1.0
 */
public interface ChapterContentService {
    ChapterContentDTO getChapterContent(Long chapterId);
    void saveChapterContent(ChapterContentDTO dto);
    String generateSmartSummary(Long chapterId);
}
