package cn.timflux.storyseek.core.story.service;

import cn.timflux.storyseek.core.story.dto.OptionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: StoryStateMachine
 * Package: cn.timflux.storyseek.core.story.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午8:04
 * @Version 1.0
 */
@Component
public class StoryStateMachine {

    private static final int MAX_CONTINUATION = 5; // N 次后触发结尾

    /**
     * 判断是否已经达到续写次数上限，若是则进入结尾逻辑
     */
    public boolean isEnding(StorySession session) {
        return session.getCount() >= MAX_CONTINUATION;
    }

    public List<OptionDTO> nextOptions(StorySession session) {
        if (isEnding(session)) {
            return List.of(); // 结尾不返回选项
        }
        // 接收无效，返回两个默认固定选项兜底
        OptionDTO a = new OptionDTO("1", "继续探险");
        OptionDTO b = new OptionDTO("2", "返回营地");
        return List.of(a, b);
    }
}

