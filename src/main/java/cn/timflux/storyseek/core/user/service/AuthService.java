package cn.timflux.storyseek.core.user.service;

import cn.timflux.storyseek.core.user.dto.LoginDTO;
import cn.timflux.storyseek.core.user.dto.RegisterDTO;
import cn.timflux.storyseek.core.user.entity.User;

/**
 * ClassName: AuthService
 * Package: cn.timflux.storyseek.core.user.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/13 下午5:14
 * @Version 1.0
 */
public interface AuthService {
    void sendCaptcha(String identifier);
    User register(RegisterDTO dto);
    User login(LoginDTO dto);
    User getUserById(Long id);
    void deleteAccount(Long userId, String password);
}