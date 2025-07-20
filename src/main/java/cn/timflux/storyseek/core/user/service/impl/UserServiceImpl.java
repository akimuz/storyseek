package cn.timflux.storyseek.core.user.service.impl;

import cn.timflux.storyseek.core.user.dto.RegisterDTO;
import cn.timflux.storyseek.core.user.entity.InspirationConsumeRecord;
import cn.timflux.storyseek.core.user.entity.InviteCode;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.mapper.InspirationConsumeRecordMapper;
import cn.timflux.storyseek.core.user.mapper.InviteCodeMapper;
import cn.timflux.storyseek.core.user.mapper.UserMapper;
import cn.timflux.storyseek.core.user.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    private final InspirationConsumeRecordMapper inspirationConsumeRecordMapper;
    private final InviteCodeMapper inviteCodeMapper;

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
    public User createUserInvite(RegisterDTO dto) {
        // 校验邀请码
        String code = dto.getInviteCode();
        if (code == null || code.isEmpty()) {
            throw new RuntimeException("邀请码不能为空");
        }
        InviteCode invite = inviteCodeMapper.selectOne(new QueryWrapper<InviteCode>()
                .eq("code", code.trim().toLowerCase())
                .eq("used", false)
        );
        if (invite == null) {
            throw new RuntimeException("邀请码无效或已被使用");
        }
        if (invite.getExpireTime() != null && invite.getExpireTime().isBefore(java.time.LocalDateTime.now().minusDays(90))) {
            throw new RuntimeException("邀请码已过期");
        }
        // 创建用户
        User user = new User();
        user.setUsername(dto.getIdentifier());
        if (dto.getIdentifier().contains("@")) user.setEmail(dto.getIdentifier());
        else user.setPhone(dto.getIdentifier());
        user.setPassword(dto.getPassword());
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setInspiration(1000000L);
        save(user);
        // 标记邀请码为已用
        invite.setUsed(true);
        invite.setUsedByUserId(user.getId());
        inviteCodeMapper.updateById(invite);
        return user;
    }

    @Override
    public Long getUserInspiration(Long userId) {
        User user = getById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        return user.getInspiration();
    }

    @Override
    @Transactional
    public void consumeInspiration(Long userId, Long consumeCount, String purpose, String requestInfo) {
        User user = getById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        if (user.getInspiration() == null || user.getInspiration() < consumeCount) {
            throw new RuntimeException("灵感值不足");
        }
        user.setInspiration(user.getInspiration() - consumeCount);
        updateById(user);
        InspirationConsumeRecord record = new InspirationConsumeRecord();
        record.setUserId(userId);
        record.setConsumeCount(consumeCount);
        record.setPurpose(purpose);
        record.setRequestInfo(requestInfo);
        inspirationConsumeRecordMapper.insert(record);
    }

    @Override
    public List<InspirationConsumeRecord> getInspirationConsumeRecords(Long userId) {
        return inspirationConsumeRecordMapper.selectList(
                new QueryWrapper<InspirationConsumeRecord>().eq("user_id", userId).orderByDesc("create_time")
        );
    }
}
