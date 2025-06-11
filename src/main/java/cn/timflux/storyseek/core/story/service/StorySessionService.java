package cn.timflux.storyseek.core.story.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: StorySessionService
 * Package: cn.timflux.storyseek.core.story.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 下午5:32
 * @Version 1.0
 */
@Service
public class StorySessionService {
    private final Map<String, StorySession> sessions = new ConcurrentHashMap<>();

    /** 创建并存储一个新会话 */
    public void addSession(StorySession session) {
        sessions.put(session.getSessionId(), session);
    }

    /** 根据 ID 获取会话，找不到返回 null */
    public StorySession getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    /** 删除会话 */
    public void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
