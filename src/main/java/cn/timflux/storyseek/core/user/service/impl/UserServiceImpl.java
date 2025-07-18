package cn.timflux.storyseek.core.user.service.impl;

import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.mapper.UserMapper;
import cn.timflux.storyseek.core.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * ClassName: UserServiceImpl
 * Package: cn.timflux.storyseek.core.user.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午6:57
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User createUser(String identifier, String passwordHash) {
        User user = new User();
        user.setUsername(identifier);
        if (identifier.contains("@")) user.setEmail(identifier);
        else user.setPhone(identifier);
        user.setPassword(passwordHash);
        user.setCreateTime(LocalDateTime.now());
        user.setInspiration(1000L);
        save(user);
        return user;
    }

    @Override
    public Long getUserInspiration(Long userId) {
        User user = getById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getInspiration();
    }
}
