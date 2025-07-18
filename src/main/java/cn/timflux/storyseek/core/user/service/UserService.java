package cn.timflux.storyseek.core.user.service;

import cn.timflux.storyseek.core.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * ClassName: UserService
 * Package: cn.timflux.storyseek.core.user.service
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午6:57
 * @Version 1.0
 */
public interface UserService extends IService<User> {
    User createUser(String identifier, String passwordHash);
    Long getUserInspiration(Long userId);
}