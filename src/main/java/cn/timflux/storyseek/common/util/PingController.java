package cn.timflux.storyseek.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: PingController
 * Package: cn.timflux.storyseek.common.util
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/8 上午3:33
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        log.info("listened ping");
        return "pong";
    }
}