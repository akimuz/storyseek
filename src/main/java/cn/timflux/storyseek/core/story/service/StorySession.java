package cn.timflux.storyseek.core.story.service;

import java.util.*;

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
     // 用于保存提交的初始上下文数据，如主角名字、风格标签等
    private Map<String, Object> context = new HashMap<>();

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

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void putContext(String choiceIdKey, String choiceId) {
        context.put(choiceIdKey,choiceId);
    }
}

