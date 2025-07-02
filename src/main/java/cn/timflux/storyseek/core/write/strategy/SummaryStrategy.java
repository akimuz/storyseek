package cn.timflux.storyseek.core.write.strategy;

import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.core.write.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.service.PromptAssemblerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: SummaryStrategy
 * Package: cn.timflux.storyseek.core.write.strategy
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午10:20
 * @Version 1.0
 */
@Component
@Slf4j
public class SummaryStrategy implements WritingStageStrategy{
    @Autowired
    private PromptAssemblerService assembler;

    @Override
    public List<ChatMessage> buildPrompt(PromptRequestDTO request) {
        log.info("构建摘要阶段 prompt，content: {}", request.getContent());

        String promptBody = assembler.assembleSummaryBody(
            request.getContent(),
            request.getCharacterCardIds(),
            request.getWorldSettingIds(),
            request.getPromptSnippetIds()
        );

        log.debug("生成的用户 prompt 内容：{}", promptBody);

        return List.of(
            ChatMessage.system("你是一位严谨逻辑强的小说编剧助手，擅长根据现有的设定和剧情整理成摘要。"),
            ChatMessage.user(promptBody)
        );
    }
}
