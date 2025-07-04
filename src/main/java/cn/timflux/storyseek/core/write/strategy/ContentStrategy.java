package cn.timflux.storyseek.core.write.strategy;

import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.core.write.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.service.PromptAssemblerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: ContentStrategy
 * Package: cn.timflux.storyseek.core.write.strategy
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:41
 * @Version 1.0
 */
@Slf4j
@Component
public class ContentStrategy implements WritingStageStrategy {

    @Autowired
    private PromptAssemblerService assembler;

    @Override
    public List<ChatMessage> buildPrompt(PromptRequestDTO request) {
        log.info("构建正文阶段 prompt，content: {}", request.getContent());

        String promptBody = assembler.assembleTextBody(
                request.getContent(),
                request.getCharacterCardIds(),
                request.getWorldSettingIds(),
                request.getPromptSnippetIds(),
                request.getRelatedSummaryIds(),
                request.getRelatedChapterIds()
        );

        log.debug("生成的用户 prompt 内容：{}", promptBody);

        return List.of(
                ChatMessage.system("你是一位小说创作专家，善于根据详细情节生成自然流畅的文本。"),
                ChatMessage.user(promptBody)
        );
    }
}