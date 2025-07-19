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
import java.util.Objects;

/**
 * ClassName: PromptAssemblerService
 * Package: cn.timflux.storyseek.core.write.service
 * Description: 负责组装不同阶段的Prompt内容，包括角色、世界观、大纲、细纲和正文等。
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:24
 * @Version 1.2
 */
@Slf4j
@Service
public class PromptAssemblerService {

    @Autowired private RoleService characterService;
    @Autowired private WorldSettingService worldService;
    @Autowired private OutlineService outlineService;
    @Autowired private DetailedOutlineService detailedOutlineService;
    @Autowired private PromptSnippetService snippetService;
    @Autowired private VolumeChapterMapper chapterMapper;

    /**
     * 统一获取并处理所有公共的提示词内容，确保空值显示为 "(无)"。
     */
    private PromptContext getCommonPromptContext(
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> snippetIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds
    ) {
        return PromptContext.builder()
            .characters(safeGetPromptText(characterService.getPromptText(characterIds)))
            .worlds(safeGetPromptText(worldService.getPromptText(worldIds)))
            .outlines(safeGetPromptText(outlineService.getPromptText(outlineIds)))
            .detailedOutlines(safeGetPromptText(detailedOutlineService.getPromptText(detailedOutlineIds)))
            .snippets(safeGetPromptText(snippetService.getPromptText(snippetIds)))
            .summaries(buildSummaryContext(summaryChapterIds))
            .chapters(buildChapterContext(chapterIds))
            .build();
    }

    /**
     * 辅助方法：如果promptText为空或空白，返回"(无)"，否则返回原文本。
     */
    private String safeGetPromptText(String promptText) {
        return StringUtils.isNotBlank(promptText) ? promptText : "(无)";
    }

    /**
     * 构建通用的提示词模板。
     *
     * @param mainRequirementTitle 主要需求部分的标题，如 "【写作要求】" 或 "【章节大纲/写作要求】"
     * @param content 具体的需求内容
     * @param context 包含所有公共提示词内容的上下文对象
     * @return 组装好的完整提示词字符串
     */
    private String buildCommonPrompt(String mainRequirementTitle, String content, PromptContext context) {
        return String.format(
            "%s\n%s\n" +
            "【前文梗概】\n%s\n" +
            "【前文正文】\n%s\n" +
            "【关联大纲】\n%s\n" +
            "【关联细纲】\n%s\n" +
            "【角色设定】\n%s\n" +
            "【世界设定】\n%s\n" +
            "【写作提示词】\n%s",
            mainRequirementTitle,
            safeGetPromptText(content),
            context.getSummaries(),
            context.getChapters(),
            context.getOutlines(),
            context.getDetailedOutlines(),
            context.getCharacters(),
            context.getWorlds(),
            context.getSnippets()
        );
    }

    /**
     * 组装角色提示词内容。
     */
    public String assembleRoleBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> chapterIds,
        List<Long> summaryChapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装角色提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下要求生成一个完整的人物角色设定。\n%s",
            buildCommonPrompt("【写作要求】", content, context)
        );
    }

    /**
     * 组装世界观提示词内容。
     */
    public String assembleWordBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装世界观提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下要求生成一个完整的世界观设定。\n%s",
            buildCommonPrompt("【写作要求】", content, context)
        );
    }

    /**
     * 组装大纲阶段的 prompt 主体。
     */
    public String assembleOutlineBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装大纲提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下要求生成一个完整的故事大纲，包含章节划分和每章一句话的简述。\n%s",
            buildCommonPrompt("【写作要求】", content, context)
        );
    }

    /**
     * 组装细纲阶段的 prompt 主体。
     */
    public String assembleDetailBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装细纲提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下大纲和要求生成一章的详细情节分段细纲，每个段落用一句话说明其要点。\n%s",
            buildCommonPrompt("【章节大纲/写作要求】", content, context)
        );
    }

    /**
     * 组装正文阶段的 prompt 主体。
     */
    public String assembleTextBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装正文提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下细纲和要求撰写一段完整小说文本，文字生动自然，语言富有表现力。\n%s",
            buildCommonPrompt("【本章细纲/写作要求】", content, context)
        );
    }

    /**
     * 组装摘要提示词内容。
     */
    public String assembleSummaryBody(
        String content,
        List<Long> characterIds,
        List<Long> worldIds,
        List<Long> outlineIds,
        List<Long> detailedOutlineIds,
        List<Long> summaryChapterIds,
        List<Long> chapterIds,
        List<Long> snippetIds
    ) {
        log.info("组装摘要提示词内容。角色卡: {}, 世界设定: {}, 大纲: {}, 细纲: {}, 提示词卡: {}, 前文梗概: {}, 前文正文: {}",
            characterIds, worldIds, outlineIds, detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        PromptContext context = getCommonPromptContext(characterIds, worldIds, outlineIds,
            detailedOutlineIds, snippetIds, summaryChapterIds, chapterIds);

        return String.format("请根据以下要求生成一个简洁、高度概括性的摘要，包括剧情情节和关键信息。\n%s",
            buildCommonPrompt("【写作要求】", content, context)
        );
    }

    /**
     * 根据章节ID列表构建前文梗概上下文。
     * 如果获取不到内容，返回 "(无)"。
     *
     * @param summaryChapterIds 章节ID列表
     * @return 格式化的前文梗概字符串
     */
    private String buildSummaryContext(List<Long> summaryChapterIds) {
        if (summaryChapterIds == null || summaryChapterIds.isEmpty()) {
            return "(无)";
        }
        List<VolumeChapter> chapters = chapterMapper.selectBatchIds(summaryChapterIds);
        if (chapters.isEmpty()) { // 即使ID不为空，查出来也可能为空
            return "(无)";
        }
        return chapters.stream()
            .filter(Objects::nonNull) // 过滤掉可能存在的空章节对象
            .sorted(Comparator.comparing(VolumeChapter::getId))
            .map(ch -> {
                String summary = StringUtils.isNotBlank(ch.getSummary()) ? ch.getSummary()
                                : StringUtils.isNotBlank(ch.getDraftSummary()) ? ch.getDraftSummary()
                                : "(本章无梗概)";
                return String.format("第%s章：%s", ch.getId(), summary);
            })
            .collect(Collectors.joining("\n"));
    }

    /**
     * 根据章节ID列表构建前文正文上下文。
     * 如果获取不到内容，返回 "(无)"。
     *
     * @param chapterIds 章节ID列表
     * @return 格式化的前文正文字符串
     */
    private String buildChapterContext(List<Long> chapterIds) {
        if (chapterIds == null || chapterIds.isEmpty()) {
            return "(无)";
        }
        List<VolumeChapter> chapters = chapterMapper.selectBatchIds(chapterIds);
        if (chapters.isEmpty()) { // 即使ID不为空，查出来也可能为空
            return "(无)";
        }
        return chapters.stream()
            .filter(Objects::nonNull) // 过滤掉可能存在的空章节对象
            .sorted(Comparator.comparing(VolumeChapter::getId))
            .map(ch -> {
                String content = StringUtils.isNotBlank(ch.getContent()) ? ch.getContent() : "(本章无正文)";
                return String.format("第%s章：\n%s", ch.getId(), content);
            })
            .collect(Collectors.joining("\n\n"));
    }

    /**
     * 辅助类：封装所有公共的Prompt上下文内容，减少方法参数数量。
     */
    @lombok.Builder
    @lombok.Getter
    private static class PromptContext {
        private String characters;
        private String worlds;
        private String outlines;
        private String detailedOutlines;
        private String snippets;
        private String summaries;
        private String chapters;
    }
}