package cn.timflux.storyseek.core.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.timflux.storyseek.common.metrics.Monitored;
import cn.timflux.storyseek.core.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserController
 * Package: cn.timflux.storyseek.core.user.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午11:54
 * @Version 1.0
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Monitored(value = "user_get_inspiration", tags = {"module=user"})
    @GetMapping("/inspiration")
    public SaResult getUserInspiration() {
        if (!StpUtil.isLogin()) {
            return SaResult.error("请先登录");
        }

        Long userId = StpUtil.getLoginIdAsLong();
        Long inspiration = userService.getUserInspiration(userId);

        return SaResult.ok().set("inspiration", inspiration);
    }

    @Monitored(value = "user_get_inspiration_records", tags = {"module=user"})
    @GetMapping("/inspiration/records")
    public SaResult getInspirationConsumeRecords() {
        if (!StpUtil.isLogin()) {
            return SaResult.error("请先登录");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        return SaResult.ok().set("records", userService.getInspirationConsumeRecords(userId));
    }
}
