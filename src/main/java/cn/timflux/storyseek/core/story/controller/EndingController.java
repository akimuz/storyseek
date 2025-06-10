package cn.timflux.storyseek.core.story.controller;

import cn.timflux.storyseek.core.story.dto.EndingRequest;
import cn.timflux.storyseek.ai.service.StoryAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: EndingController
 * Package: cn.timflux.storyseek.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/10 上午7:53
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/story")
public class EndingController {

    private final StoryAIService storyAIService;

    @Autowired
    public EndingController(StoryAIService storyAIService) {
        this.storyAIService = storyAIService;
    }

    @PostMapping(value = "/ending", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateEnding(@RequestBody EndingRequest req) {
        Map<String, Object> context = new HashMap<>();
        context.put("fullStory", req.getFullStory());
        return storyAIService.generateEnding(context);
    }
}
