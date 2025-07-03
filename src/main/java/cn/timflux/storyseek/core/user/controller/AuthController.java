package cn.timflux.storyseek.core.user.controller;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.timflux.storyseek.common.api.ApiResponse;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.service.CaptchaService;
import cn.timflux.storyseek.core.user.service.UserService;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final UserService userService;
    private final CaptchaService captchaService;

    public AuthController(UserService userService, CaptchaService captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    // 发送验证码接口
    @PostMapping("/send-captcha")
    public SaResult sendCaptcha(@RequestParam String phone) {
        captchaService.sendCaptcha(phone);
        return SaResult.ok("验证码已发送");
    }

    // 注册 DTO
    @Data
    public static class RegisterDTO {
        private String phone;
        private String password;
        private String captcha;
    }

    // 登录 DTO
    @Data
    public static class LoginDTO {
        private String phone;
        private String password; // 选填
        private String captcha;  // 选填
    }

    @PostMapping("/register")
    public SaResult register(@RequestBody RegisterDTO dto) {
        try {
            boolean ok = userService.register(dto.getPhone(), dto.getPassword(), dto.getCaptcha());

            if (ok) {
                User user = userService.getByPhone(dto.getPhone());
                if (user == null) return SaResult.error("用户注册后查询失败");

                StpUtil.login(user.getId());

                return SaResult.ok("注册成功").setData(Map.of(
                    "userId", user.getId(),
                    "username", user.getPhone()
                ));
            } else {
                return SaResult.error("注册失败");
            }
        } catch (RuntimeException e) {
            return SaResult.error(e.getMessage());
        }
    }


    @PostMapping("/login")
    public SaResult login(@RequestBody LoginDTO dto) {
        try {
            boolean ok;
            User user;
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                ok = userService.loginWithPassword(dto.getPhone(), dto.getPassword());
            } else if (dto.getCaptcha() != null && !dto.getCaptcha().isEmpty()) {
                ok = userService.loginWithCaptcha(dto.getPhone(), dto.getCaptcha());
            } else {
                return SaResult.error("密码或验证码必须填写一个");
            }

            if (!ok) {
                return SaResult.error("登录失败");
            }

            // 登录成功，获取用户信息
            user = userService.getByPhone(dto.getPhone()); // 通过手机号查用户

            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());

            return SaResult.data(data).setMsg("登录成功");
        } catch (RuntimeException e) {
            return SaResult.error(e.getMessage());
        }
    }


    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-store, no-cache, must-revalidate, max-age=0");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userService.getById(userId);

            if (user == null) {
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(ApiResponse.error("用户不存在"));
            }

            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("username", user.getUsername());

            return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.ok(data));
        } else {
            return ResponseEntity.ok()
                .headers(headers)
                .body(ApiResponse.error("未登录"));
        }
    }



    @GetMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok("已注销");
    }
}
