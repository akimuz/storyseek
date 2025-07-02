package cn.timflux.storyseek.core.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.mapper.UserMapper;
import cn.timflux.storyseek.core.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import cn.timflux.storyseek.core.user.service.CaptchaService;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private CaptchaService captchaService;

    @Override
    public boolean register(String phone, String rawPassword, String captcha) {
        if (!captchaService.verifyCaptcha(phone, captcha)) {
            throw new RuntimeException("验证码错误");
        }

        if (userExists(phone)) {
            throw new RuntimeException("手机号已存在");
        }

        String hashed = SaSecureUtil.md5(rawPassword);
        User u = new User();
        u.setUsername(phone);
        u.setPhone(phone);
        u.setPassword(hashed);
        u.setCreateTime(LocalDateTime.now());
        u.setInspiration(1000L); // 注册初始代币
        boolean saved = save(u);
        if (saved) {
            captchaService.removeCaptcha(phone);
        }
        return saved;
    }

    @Override
    public boolean loginWithPassword(String phone, String rawPassword) {
        User u = lambdaQuery().eq(User::getPhone, phone).one();
        if (u == null) {
            throw new RuntimeException("用户不存在，请先注册");
        }
        String hashed = SaSecureUtil.md5(rawPassword);
        if (!hashed.equals(u.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        StpUtil.login(u.getId());
        return true;
    }

    @Override
    public boolean loginWithCaptcha(String phone, String captcha) {
        if (!captchaService.verifyCaptcha(phone, captcha)) {
            throw new RuntimeException("验证码错误");
        }
        User u = lambdaQuery().eq(User::getPhone, phone).one();
        if (u == null) {
            throw new RuntimeException("用户不存在，请先注册");
        }
        StpUtil.login(u.getId());
        captchaService.removeCaptcha(phone);
        return true;
    }

    @Override
    public boolean userExists(String phone) {
        return lambdaQuery().eq(User::getUsername, phone).exists();
    }

    @Override
    public Long getUserInspiration(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getInspiration();
    }

    @Override
    public User getByPhone(String phone) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("phone", phone));
    }

}
