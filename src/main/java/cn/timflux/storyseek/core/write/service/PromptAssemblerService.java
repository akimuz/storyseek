package cn.timflux.storyseek.core.write.service;

import cn.timflux.storyseek.core.write.entity.VolumeChapter;
import cn.timflux.storyseek.core.write.mapper.VolumeChapterMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: PromptAssemblerService
 * Package: cn.timflux.storyseek.core.write.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:24
 * @Version 1.0
 */
@Slf4j
@Service
public class PromptAssemblerService {

    @Autowired private RoleService characterService;
    @Autowired private WorldSettingService worldService;
    @Autowired private PromptSnippetService snippetService;
    @Autowired private VolumeChapterMapper chapterMapper;


    public String assembleRoleBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds
    ) {
        log.info("组装角色提示词内容。角色卡: {}, 世界设定: {}, 提示词卡: {}", characterIds, worldIds, snippetIds);

        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);

        return String.format(
            "请根据以下要求生成一个完整的人物角色设定。\n" +
            "【写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            content,
            characters,
            worlds,
            snippets
        );
    }

    public String assembleWordBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds
    ) {
        log.info("组装世界观提示词内容。角色卡: {}, 世界设定: {}, 提示词卡: {}", characterIds, worldIds, snippetIds);

        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);

        return String.format(
            "请根据以下要求生成一个完整的世界观设定。\n" +
            "【写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            content,
            characters,
            worlds,
            snippets
        );
    }

    /**
     * 组装大纲阶段的 prompt 主体
     */
    public String assembleOutlineBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds
    ) {
        log.info("组装大纲提示词内容。角色卡: {}, 世界设定: {}, 提示词卡: {}", characterIds, worldIds, snippetIds);

        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);

        return String.format(
            "请根据以下要求生成一个完整的故事大纲，包含章节划分和每章一句话的简述。\n" +
            "【写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            content,
            characters,
            worlds,
            snippets
        );
    }

        /**
     * 组装细纲阶段的 prompt 主体
     */
    public String assembleDetailBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds
    ) {
        log.info("组装细纲提示词内容");
        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);

        return String.format(
            "请根据以下大纲和要求生成一章的详细情节分段细纲，每个段落用一句话说明其要点。\n" +
            "【章节大纲/写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            content,
            characters,
            worlds,
            snippets
        );
    }

    /**
     * 组装正文阶段的 prompt 主体
     */
    public String assembleTextBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds
    ) {
        log.info("组装正文提示词内容");
        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);
        String summaries = buildSummaryContext(summaryChapterIds);
        String chapters = buildChapterContext(chapterIds);
        return String.format(
            "请根据以下细纲和要求撰写一段完整小说文本，文字生动自然，语言富有表现力。\n" +
            "【前文梗概】\n%s\n" +
            "【前文正文】\n%s\n" +
            "【本章细纲/写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            summaries,
            chapters,
            content,
            characters,
            worlds,
            snippets
        );
    }

    public String assembleSummaryBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> snippetIds
    ) {
        log.info("组装摘要提示词内容。角色卡: {}, 世界设定: {}, 提示词卡: {}", characterIds, worldIds, snippetIds);

        String characters = characterService.getPromptText(characterIds);
        String worlds = worldService.getPromptText(worldIds);
        String snippets = snippetService.getPromptText(snippetIds);

        return String.format(
            "请根据以下要求生成一个简洁、高度概括性的摘要，包括剧情情节和关键信息。\n" +
            "【写作要求】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            content,
            characters,
            worlds,
            snippets
        );
    }

    private String buildSummaryContext(List<Long> summaryChapterIds) {
        if (summaryChapterIds == null || summaryChapterIds.isEmpty()) return "(无)";

        List<VolumeChapter> chapters = chapterMapper.selectBatchIds(summaryChapterIds);
        return chapters.stream()
            .sorted(Comparator.comparing(VolumeChapter::getId)) // 保证顺序一致
            .map(ch -> {
                String summary = StringUtils.isNotBlank(ch.getSummary()) ? ch.getSummary()
                                : StringUtils.isNotBlank(ch.getDraftSummary()) ? ch.getDraftSummary()
                                : "(本章无梗概)";
                return String.format("第%s章：%s", ch.getId(), summary);
            })
            .collect(Collectors.joining("\n"));
    }

    private String buildChapterContext(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) return "(无)";
        List<VolumeChapter> chapters = chapterMapper.selectBatchIds(chapterIds);
        return chapters.stream()
            .sorted(Comparator.comparing(VolumeChapter::getId))
            .map(ch -> {
                String content = StringUtils.isNotBlank(ch.getContent()) ? ch.getContent() : "(本章无正文)";
                return String.format("第%s章：\n%s", ch.getId(), content);
            })
            .collect(Collectors.joining("\n\n"));
    }

}
