// src/main/java/cn/timflux/storyseek/core/story/controller/BeginningController.java
package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.BeginningRequest;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StorySessionService;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
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
    private final StorySessionService sessionService;

    public BeginningController(StoryAIService storyAIService,
                               StoryStateMachine stateMachine,
                               StorySessionService sessionService) {
        this.storyAIService = storyAIService;
        this.stateMachine = stateMachine;
        this.sessionService = sessionService;
    }

    /**
     * 1. 接收表单，创建会话并返回 sessionId
     */
    @PostMapping("/beginning")
    public Map<String, String> createSession(@RequestBody BeginningRequest req) {
        StorySession session = new StorySession();
        session.setContext(Map.of(
            "heroName", req.getHeroName(),
            "styleTag", req.getStyleTag(),
            "worldSetting", req.getWorldSetting(),
            "otherReq", req.getOtherReq()
        ));
        sessionService.addSession(session);
        return Map.of("sessionId", session.getSessionId());
    }

    /**
     * 2. SSE 流式返回：先 content 事件推剧情片段，再 options 事件推选项
     */
    @GetMapping("/stream/{sessionId}")
    public void streamBeginning(@PathVariable String sessionId,
                                HttpServletResponse response) throws IOException {
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(404, "Session not found");
            return;
        }

        // 设置 SSE 响应头，并强制 UTF-8 编码
        response.setContentType("text/event-stream; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        ServletOutputStream out = response.getOutputStream();

        // 获取上下文并调用 AI 生成 Flux<String>
        Map<String, Object> ctx = session.getContext();
        Iterator<String> iterator = storyAIService.generateBeginning(ctx)
            .doOnNext(session::addSegment)
            .toStream()
            .iterator();

        // 用于分割 [[OPTIONS]] 后续 JSON
        List<String> jsonBuf = new ArrayList<>();
        boolean seenSep = false, optionEnd = false;

        // 1. 逐片段写 content 事件
        while (iterator.hasNext()) {
            String fragment = iterator.next();

            try {
                if (!seenSep && fragment.contains("[[")) {
                    int idx = fragment.indexOf("[[");
                    String before = fragment.substring(0, idx);
                    String after  = fragment.substring(idx + "[[".length());
                    seenSep = true;
                    jsonBuf.add(after);
                    System.out.println("fragment1 after:"+after);
                    System.out.println("fragment1 before:"+before);
                    writeEvent(out, "content", before);
                }
                // 选项收集
                else if (seenSep && fragment.contains("]]")) {
                    int end = fragment.indexOf("]]");
                    String after  = fragment.substring(end + "]]".length());
                    jsonBuf.add(after);
                    optionEnd = true;
                }
                else if(seenSep && optionEnd){
                    jsonBuf.add(fragment);
                    System.out.println("选项开始: " + fragment);
                }
                else if(!seenSep){
                    System.out.println("fragment3:"+fragment);
                    writeEvent(out, "content", fragment);
                }
            }
            catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        // 2. 生成并写 options 事件
        List<OptionDTO> opts;
        if (!seenSep) {
            opts = stateMachine.nextOptions(session);
        } else {
            String json = String.join("", jsonBuf);
            System.out.println("json:"+json);
            try {
                opts = mapper.readValue(json, new TypeReference<>() {});
                System.out.println("opts:"+opts);
            } catch (Exception e) {
                opts = List.of();
            }
        }
        Map<String, Object> payload = Map.of(
            "ending", false,
            "options", opts,
            "sessionId", sessionId
        );
        String data;
        try {
            data = mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        writeEvent(out, "options", data);
        System.out.println("data:"+ data);

        // 刷新但不关闭，让客户端决定何时断开
        out.flush();
    }

    /**
     * 辅助：写一条 SSE 事件
     */
    private void writeEvent(ServletOutputStream out,
                            String event, String data) throws IOException {
        String sse = "event: " + event + "\n" +
                     "data: "  + data  + "\n\n";
        out.write(sse.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
