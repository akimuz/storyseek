package cn.timflux.storyseek.core.write.strategy;

import cn.timflux.storyseek.ai.model.dto.ChatMessage;
import cn.timflux.storyseek.core.write.edit.dto.PromptRequestDTO;

import java.util.List;

/**
 * ClassName: WritingStageStrategy
 * Package: cn.timflux.storyseek.core.write.strategy
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午12:20
 * @Version 1.0
 */
public interface WritingStageStrategy {
    /**
     * 构造用于大模型生成的 Prompt 消息序列
     */
    List<ChatMessage> buildPrompt(PromptRequestDTO request);
}
