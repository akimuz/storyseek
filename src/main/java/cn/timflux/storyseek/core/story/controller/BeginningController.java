// src/main/java/cn/timflux/storyseek/core/story/controller/BeginningController.java
package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.BeginningRequest;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName: BeginningController
 * Package: cn.timflux.storyseek.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:49
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/story")
public class BeginningController {

    private final StoryAIService storyAIService;
    private final StoryStateMachine stateMachine;
    private final ObjectMapper mapper = new ObjectMapper();
    // 会话存储
    private final Map<String, StorySession> sessions = new ConcurrentHashMap<>();

    public BeginningController(StoryAIService storyAIService,
                               StoryStateMachine stateMachine) {
        this.storyAIService = storyAIService;
        this.stateMachine = stateMachine;
    }

    @PostMapping(value = "/beginning", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> generateBeginning(
            @RequestBody BeginningRequest req
    ) {
        // 1. 新建会话并持久 sessionId
        StorySession session = new StorySession();
        sessions.put(session.getSessionId(), session);

        // 2. 调用 AI，得到原始流
        Map<String, Object> ctx = Map.of(
            "heroName", req.getHeroName(),
            "styleTag", req.getStyleTag(),
            "worldSetting", req.getWorldSetting(),
            "otherReq", req.getOtherReq()
        );
        Flux<String> raw = storyAIService.generateBeginning(ctx)
                .doOnNext(session::addSegment);

        // 3. 解析分隔符 [[OPTIONS]]（如果你在 prompt 中要求模型返回选项）
        List<String> jsonBuf = new ArrayList<>();
        final boolean[] seenSep = {false};
        boolean ending = false; // 开头阶段不判结尾

        Flux<ServerSentEvent<String>> contentEvents = raw
            .map(fragment -> {
                if (seenSep[0]) {
                    jsonBuf.add(fragment);
                    return null;
                }
                int idx = fragment.indexOf("[[OPTIONS]]");
                if (idx >= 0) {
                    seenSep[0] = true;
                    String before = fragment.substring(0, idx);
                    String after  = fragment.substring(idx + "[[OPTIONS]]".length());
                    jsonBuf.add(after);
                    return ServerSentEvent.<String>builder()
                        .event("content")
                        .data(before)
                        .build();
                }
                return ServerSentEvent.<String>builder()
                        .event("content")
                        .data(fragment)
                        .build();
            })
            .filter(Objects::nonNull);

        // 4. 拼接 options 事件：同续写
        Flux<ServerSentEvent<String>> optionsEvent = Flux.defer(() -> {
            List<OptionDTO> opts;
            if (!seenSep[0]) {
                // 如果开头没有让模型生成选项，使用 stateMachine 给出的默认
                opts = stateMachine.nextOptions(session);
            } else {
                // 从 jsonBuf 解析
                try {
                    String json = String.join("", jsonBuf);
                    opts = mapper.readValue(json, new TypeReference<>() {});
                } catch (Exception e) {
                    opts = Collections.emptyList();
                }
            }
            String data = null;
            try {
                data = mapper.writeValueAsString(Map.of(
                    "ending", ending,
                    "options", opts
                ));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return Flux.just(ServerSentEvent.<String>builder()
                    .event("options")
                    .data(data)
                    .build());
        });

        // 5. 将 sessionId 放在第一个 event 的 header 中
        //    (前端可从响应头里读到 Session-Id)
        return contentEvents.concatWith(optionsEvent);
    }
}
