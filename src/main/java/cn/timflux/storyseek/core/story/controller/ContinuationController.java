package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.ContinuationRequest;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: ContinuationController
 * Package: cn.timflux.storyseek.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:52
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/story")
public class ContinuationController {

    private final StoryAIService storyAIService;
    private final StoryStateMachine stateMachine;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, StorySession> sessions = new ConcurrentHashMap<>();

    public ContinuationController(StoryAIService storyAIService,
                                  StoryStateMachine stateMachine) {
        this.storyAIService = storyAIService;
        this.stateMachine = stateMachine;
    }

    @PostMapping(value = "/continuation", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generateContinuation(
            @RequestHeader("X-Session-Id") String sessionId,
            @RequestBody ContinuationRequest req
    ) {
        // 1. 获取或新建会话
        StorySession session = sessions
                .computeIfAbsent(sessionId, k -> new StorySession());

        // 2. 增加次数，用于判定结尾
        session.incrementAndGetCount();
        boolean ending = stateMachine.isEnding(session);

        // 3. 调用 AI 策略，拿到 raw flux
        Flux<String> raw = storyAIService.generateContinuation(Map.of(
                "currentStory", req.getCurrentStory(),
                "choiceId", req.getChoiceId()
        )).doOnNext(session::addSegment);

        // 4. 解析流：遇到 [[OPTIONS]] 后，jsonBuf 缓存后续 JSON
        List<String> jsonBuf = new ArrayList<>();
        final boolean[] seenSep = {false};

        return raw
            .map(fragment -> {
                if (seenSep[0]) {
                    // 选项部分，累积 JSON
                    jsonBuf.add(fragment);
                    return null; // 不直接输出，这里暂时不发
                }
                // 搜索分隔符
                int idx = fragment.indexOf("[[OPTIONS]]");
                if (idx >= 0) {
                    seenSep[0] = true;
                    // 输出这一段前的内容
                    String before = fragment.substring(0, idx);
                    // 余下作为首块 JSON
                    String after = fragment.substring(idx + "[[OPTIONS]]".length());
                    jsonBuf.add(after);
                    return ServerSentEvent.<String>builder()
                        .event("content")
                        .data(before)
                        .build();
                }
                // 普通内容
                return ServerSentEvent.<String>builder()
                        .event("content")
                        .data(fragment)
                        .build();
            })
            // 过滤掉 null
            .filter(Objects::nonNull)
            // 当看到分隔符后，紧接着拼接 JSON 并发送 options 事件，然后根据 ending 判断是否关闭
            .concatWith(Flux.defer(() -> {
                // 5. 拼全 JSON 并反序列化
                String json = String.join("", jsonBuf);
                List<OptionDTO> options;
                try {
                    options = mapper.readValue(json, new TypeReference<>() {});
                } catch (Exception e) {
                    // 解析失败时，fallback: 空列表
                    options = Collections.emptyList();
                }
                // 6. 构造 options 事件
                ServerSentEvent<String> optEvent = null;
                try {
                    optEvent = ServerSentEvent.<String>builder()
                            .event("options")
                            .data(mapper.writeValueAsString(Map.of(
                                "ending", ending,
                                "options", options
                            )))
                            .build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return Flux.just(optEvent);
            }));
    }
}
