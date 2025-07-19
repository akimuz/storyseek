package cn.timflux.storyseek.core.write.strategy;

import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.core.write.dto.PromptRequestDTO;
import cn.timflux.storyseek.core.write.service.PromptAssemblerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: RoleStrategy
 * Package: cn.timflux.storyseek.core.write.strategy
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 下午8:33
 * @Version 1.0
 */
@Slf4j
@Component
public class RoleStrategy implements WritingStageStrategy{
    @Autowired
    private PromptAssemblerService assembler;

    @Override
    public List<ChatMessage> buildPrompt(PromptRequestDTO request) {
        log.info("构建角色阶段 prompt，content: {}", request.getContent());
        log.info("request检查" + request.getPromptSnippetIds());
        String promptBody = assembler.assembleRoleBody(
            request.getContent(),
            request.getCharacterCardIds(),
            request.getWorldSettingIds(),
            request.getOutlineIds(),
            request.getDetailOutlineIds(),
            request.getRelatedChapterIds(),
            request.getRelatedSummaryIds(),
            request.getPromptSnippetIds()
        );

        log.debug("生成的用户 prompt 内容：{}", promptBody);

        return List.of(
            ChatMessage.system("你是一位富有创意的小说编剧助手，擅长人物设定。"),
            ChatMessage.user(promptBody)
        );
    }
}
