package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.ContinuationRequest;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StorySessionService;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

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

    private static final String HEADER_SESSION = "X-Session-Id";

    private final StoryAIService storyAIService;
    private final StoryStateMachine stateMachine;
    private final StorySessionService sessionService;
    private final ObjectMapper mapper = new ObjectMapper();

    public ContinuationController(StoryAIService storyAIService,
                                  StoryStateMachine stateMachine,
                                  StorySessionService sessionService) {
        this.storyAIService = storyAIService;
        this.stateMachine = stateMachine;
        this.sessionService = sessionService;
    }

    /**
     * 第一步：用户选择一个选项
     */
    @PostMapping("/continuation")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void chooseOption(@RequestHeader(HEADER_SESSION) String sessionId,
                             @RequestBody ContinuationRequest req,
                             HttpServletResponse response) throws IOException {
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }
        session.putContext("choiceId", req.getChoiceId());
    }

    /**
     * 第二步：流式返回续写或进入结尾
     */
    @GetMapping("/continuation/stream/{sessionId}")
    public void streamContinuation(@PathVariable String sessionId,
                                   @RequestParam(required = false) String currentStory,
                                   HttpServletResponse response) throws IOException {
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Session not found");
            return;
        }

        // 增加续写计数并判断是否该结束
        int round = session.incrementAndGetCount();
        boolean ending = stateMachine.isEnding(session);

        // SSE 头部设置
        response.setContentType("text/event-stream; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        ServletOutputStream out = response.getOutputStream();

        // 准备上下文，注入 previousStory 与 choiceId
        Map<String, Object> ctx = new HashMap<>(session.getContext());
        if (currentStory != null) {
            ctx.put("currentStory", currentStory);
        }

        // 根据是否结尾，调用不同的 AI 接口
        Iterator<String> iterator = ending
            ? storyAIService.generateEnding(ctx).toStream().iterator()
            : storyAIService.generateContinuation(ctx).toStream().iterator();

        // SSE 事件分割处理
        boolean seenSep = false, optionEnd = false;
        List<String> jsonBuf = new ArrayList<>();

        while (iterator.hasNext()) {
            String fragment = iterator.next();
            try {
                if (!seenSep && fragment.contains("[[")) {
                    int idx = fragment.indexOf("[[");
                    // content 之前部分
                    writeEvent(out, "content", fragment.substring(0, idx));
                    seenSep = true;
                    jsonBuf.add(fragment.substring(idx + 2));
                }
                else if (seenSep && !optionEnd && fragment.contains("]]")) {
                    int idx = fragment.indexOf("]]");
                    jsonBuf.add(fragment.substring(0, idx));
                    optionEnd = true;
                    jsonBuf.add(fragment.substring(idx + 2));
                }
                else if (seenSep) {
                    jsonBuf.add(fragment);
                }
                else {
                    writeEvent(out, "content", fragment);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        // 组织 options
        List<OptionDTO> opts;
        if (ending) {
            opts = List.of();  // 结尾不展示选项
        } else if (!seenSep) {
            opts = stateMachine.nextOptions(session);
        } else {
            // 从缓冲 JSON 读取
            String json = String.join("", jsonBuf);
            try {
                opts = mapper.readValue(json, new TypeReference<List<OptionDTO>>() {});
            } catch (Exception ex) {
                opts = stateMachine.nextOptions(session);
            }
        }

        // 发送 options 事件（结尾标记）
        Map<String, Object> payload = Map.of(
            "ending", ending,
            "options", opts,
            "sessionId", sessionId
        );
        String data = mapper.writeValueAsString(payload);
        writeEvent(out, "options", data);
        out.flush();
    }

    private void writeEvent(ServletOutputStream out, String event, String data) throws IOException {
        out.write(("event: " + event + "\n").getBytes(StandardCharsets.UTF_8));
        out.write(("data: "  + data  + "\n\n").getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
