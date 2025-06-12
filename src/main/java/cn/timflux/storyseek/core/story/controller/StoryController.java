package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.BeginningRequest;
import cn.timflux.storyseek.core.story.dto.OptionDTO;
import cn.timflux.storyseek.core.story.service.StorySession;
import cn.timflux.storyseek.core.story.service.StorySessionService;
import cn.timflux.storyseek.core.story.service.StoryStateMachine;
import cn.timflux.storyseek.ai.service.StoryAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * ClassName: StoryController
 * Package: cn.timflux.storyseek.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/11 下午1:06
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/story")
public class StoryController {

    private final StoryAIService aiService;
    private final StoryStateMachine stateMachine;
    private final StorySessionService sessionService;
    private final ObjectMapper mapper = new ObjectMapper();

    public StoryController(StoryAIService aiService,
                           StoryStateMachine stateMachine,
                           StorySessionService sessionService) {
        this.aiService = aiService;
        this.stateMachine = stateMachine;
        this.sessionService = sessionService;
    }

    /** 1. 初始化会话，返回 sessionId */
    @PostMapping("/beginning")
    public Map<String, String> start(@RequestBody BeginningRequest req) {
        StorySession session = new StorySession();
        session.setContext(Map.of(
            "heroName", req.getHeroName(),
            "styleTag", req.getStyleTag(),
            "worldSetting", req.getWorldSetting(),
            "otherReq", req.getOtherReq(),
            // 首次没有选项
            "lastOptions", List.of()
        ));
        sessionService.addSession(session);
        return Map.of("sessionId", session.getSessionId());
    }

    /** 2. SSE 流式生成（包含：开头→续写→结尾） */
    @GetMapping("/stream/{sessionId}")
    public void stream(@PathVariable String sessionId,
                       @RequestParam(value="choiceId", required=false) String choiceId,
                       HttpServletResponse response) throws IOException {
        System.out.println("choiceId:" + choiceId);
        StorySession session = sessionService.getSession(sessionId);
        if (session == null) {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Session not found");
            return;
        }

        // 若传入了 choiceId，先存起来
        if (choiceId != null) {
            session.putContext("lastChoice", choiceId);
        }

        // 1. 先判断当前是否为结尾
        boolean frontEnding = stateMachine.frontEnding(session);
        boolean genEnding = stateMachine.genEnding(session);
        // 2. 再增加续写次数，并把 turn 放入上下文
        int turn = session.incrementAndGetCount();

        // 统一 SSE Header
        response.setContentType("text/event-stream; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        ServletOutputStream out = response.getOutputStream();

        // 构建 AI 上下文
        Map<String, Object> ctx = new HashMap<>(session.getContext());
        ctx.put("sessionId",sessionId);
        System.out.println("ctx"+ctx);

        // 选 AI 服务方法
        Iterator<String> it = genEnding
            ? aiService.generateEnding(ctx).doOnNext(session::addSegment).toStream().iterator()    // Flux<String> → Stream<String>
            : (turn == 1
                ? aiService.generateBeginning(ctx)
                : aiService.generateContinuation(ctx))
            .doOnNext(session::addSegment)
            .toStream().iterator();

        // SSE 处理
        List<String> buf = new ArrayList<>();
        boolean seenSep = false;
        while (it.hasNext()) {
            String frag = it.next();
            // 未收集选项 且找到占位符
            if (!seenSep && frag.contains("&")) {
                int idx = frag.indexOf("&");
                writeEvent(out, "content", frag.substring(0, idx));
                buf.add(frag.substring(idx + 1));
                seenSep = true;
            }
            else if (seenSep) {
                buf.add(frag);
            }
            else{
                writeEvent(out, "content", frag);
            }
        }

        // 解析选项
        List<OptionDTO> opts;
        if (!seenSep) { // 从大模型返回中 未收集到选项
            opts = stateMachine.nextOptions(session);
        } else {
            String json = String.join("", buf);
            try {
                opts = mapper.readValue(json, mapper.getTypeFactory()
                    .constructCollectionType(List.class, OptionDTO.class));
            } catch (Exception e) {
                opts = Collections.emptyList();
            }
        }

        // 存入会话上下文，前端可在下次请求前取到
        session.putContext("lastOptions", opts);
        // SSE 推送 options
        Map<String,Object> payload = Map.of(
            "ending", frontEnding,
            "options", opts,
            "sessionId", sessionId
        );
        writeEvent(out, "options", mapper.writeValueAsString(payload));
        out.flush();
    }

    private void writeEvent(ServletOutputStream out, String evt, String data) throws IOException {
        out.write(("event: "+evt+"\n"+"data: "+data+"\n\n")
            .getBytes(StandardCharsets.UTF_8));
        out.flush();
    }
}
