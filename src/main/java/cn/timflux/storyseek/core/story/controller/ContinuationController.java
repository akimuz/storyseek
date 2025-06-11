package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.ContinuationRequest;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StorySessionService;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

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

    private final StoryAIService storyAIService;
    private final StoryStateMachine stateMachine;
    private final ObjectMapper mapper = new ObjectMapper();
    private final StorySessionService sessionService;

    @Autowired
    public ContinuationController(StoryAIService storyAIService,
                                  StoryStateMachine stateMachine,
                                  StorySessionService sessionService) {
        this.storyAIService = storyAIService;
        this.stateMachine = stateMachine;
        this.sessionService = sessionService;
    }

    /**
     * POST 接口：仅接收用户选择并存入会话
     */
    @PostMapping("/continuation")
    public void chooseOption(@RequestHeader("X-Session-Id") String sessionId,
                             @RequestBody ContinuationRequest req,
                             HttpServletResponse response) throws IOException {
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(404, "Session not found");
            return;
        }
        // 存储用户选择
        session.putContext("choiceId", req.getChoiceId());
        response.setStatus(204);
    }

    @GetMapping("continuation/stream/{sessionId}")
    public void streamContinuation(@PathVariable String sessionId,
                               @RequestParam(required = false) String currentStory,
                               HttpServletResponse response) throws IOException {
        // 1. 获取会话
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(404, "Session not found");
            return;
        }

        // 2. 增加续写次数并检查是否结束
        int ct = session.incrementAndGetCount();
        boolean ending = stateMachine.isEnding(session);

        // 3. 设置SSE响应头
        response.setContentType("text/event-stream; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        ServletOutputStream out = response.getOutputStream();

        // 4. 构建上下文
        Map<String, Object> ctx = new HashMap<>(session.getContext());
        if (currentStory != null) {
            ctx.put("currentStory", currentStory);
        }
        String choiceId = (String) session.getContext().get("choiceId");
        ctx.put("choiceId", choiceId);

        System.out.println("第"+ct +"轮次");
        // 5. 生成续写内容
        Iterator<String> iterator = storyAIService.generateContinuation(ctx)
            .doOnNext(session::addSegment)
            .toStream()
            .iterator();

        // 6. 处理续写内容和选项
        List<String> jsonBuf = new ArrayList<>();
        boolean seenSep = false;
        boolean optionEnd = false;

        while (iterator.hasNext()) {
            String fragment = iterator.next();
            try {
                if (!seenSep && fragment.contains("[[")) {
                    int idx = fragment.indexOf("[[");
                    String before = fragment.substring(0, idx);
                    String after = fragment.substring(idx + "[[".length());
                    seenSep = true;
                    jsonBuf.add(after);
                    writeEvent(out, "content", before);
                } else if (seenSep && fragment.contains("]]")) {
                    int end = fragment.indexOf("]]");
                    String after = fragment.substring(end + "]]".length());
                    jsonBuf.add(after);
                    optionEnd = true;
                } else if (seenSep && optionEnd) {
                    jsonBuf.add(fragment);
                } else if (!seenSep) {
                    writeEvent(out, "content", fragment);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        // 7. 处理选项
        List<OptionDTO> opts;
        if (!seenSep) {
            opts = stateMachine.nextOptions(session);
        } else {
            String json = String.join("", jsonBuf);
            try {
                opts = mapper.readValue(json, new TypeReference<List<OptionDTO>>() {});
            } catch (Exception e) {
                opts = Collections.emptyList();
            }
        }

        // 8. 发送选项事件
        Map<String, Object> payload = Map.of(
            "ending", ending,
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

        // 9. 刷新输出流
        out.flush();
    }

    private void writeEvent(ServletOutputStream out,
                            String event, String data) throws IOException {
        String sse = "event: " + event + "\n" +
                     "data: "  + data  + "\n\n";
        out.write(sse.getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}