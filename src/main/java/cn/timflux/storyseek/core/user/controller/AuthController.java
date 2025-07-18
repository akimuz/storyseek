package cn.timflux.storyseek.core.user.controller;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.timflux.storyseek.core.user.dto.DeleteAccountDTO;
import cn.timflux.storyseek.core.user.dto.LoginDTO;
import cn.timflux.storyseek.core.user.dto.RegisterDTO;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ClassName: AuthController
 * Package: cn.timflux.storyseek.core.user.controller
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:01
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-captcha")
    public SaResult sendCaptcha(@RequestParam String identifier) {
        log.info("发送验证码请求：{}", identifier);
        authService.sendCaptcha(identifier);
        return SaResult.ok("验证码已发送");
    }

    @PostMapping("/register")
    public SaResult register(@RequestBody RegisterDTO dto) {
        User user = authService.register(dto);
        StpUtil.login(user.getId());
        return SaResult.ok("注册成功").setData(Map.of(
                "userId", user.getId().toString(),
                "username", user.getUsername()
        ));
    }

    @PostMapping("/delete-account")
    public SaResult deleteAccount(@RequestBody DeleteAccountDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        authService.deleteAccount(userId, dto.getPassword());
        StpUtil.logout(); // 删除后自动登出
        return SaResult.ok("账号已永久删除");
    }

    @PostMapping("/login")
    public SaResult login(@RequestBody LoginDTO dto) {
        User user = authService.login(dto);
        StpUtil.login(user.getId());
        return SaResult.ok("登录成功").setData(Map.of(
                "userId", user.getId().toString(),
                "username", user.getUsername()
        ));
    }

    @GetMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("已注销");
    }

    @GetMapping("/status")
    public SaResult status() {
        if (!StpUtil.isLogin()) return SaResult.error("未登录");
        Long userId = StpUtil.getLoginIdAsLong();
        User user = authService.getUserById(userId);
        return SaResult.ok().setData(Map.of(
                "userId", userId.toString(),
                "username", user.getUsername()
        ));
    }
}