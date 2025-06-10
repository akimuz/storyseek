package cn.timflux.storyseek.core.story.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ClassName: StorySession
 * Package: cn.timflux.storyseek.ai.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午8:02
 * @Version 1.0
 */
public class StorySession {
    private final String sessionId = UUID.randomUUID().toString();
    private final List<String> history = new ArrayList<>();
    private int continuationCount = 0;

    public String getSessionId() {
        return sessionId;
    }
    public void addSegment(String segment) {
        history.add(segment);
    }
    public List<String> getHistory() {
        return history;
    }
    public int incrementAndGetCount() {
        return ++continuationCount;
    }
    public int getCount() {
        return continuationCount;
    }
}

