package cn.timflux.storyseek.ai.model.core.story.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: DemoController
 * Package: cn.timflux.storyseek.ai.model.core.story.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/9 下午10:23
 * @Version 1.0
 */

@RestController
public class DemoController {

    @Autowired
    private OpenAiChatModel chatModel;

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "message", defaultValue = "Hello")
                            String message){
        String result = chatModel.call(message);
        System.out.println(result);
        return result;
    }
}