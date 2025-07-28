package cn.timflux.storyseek.core.user.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.timflux.storyseek.core.user.dto.LoginDTO;
import cn.timflux.storyseek.core.user.dto.RegisterDTO;
import cn.timflux.storyseek.core.user.entity.User;
import cn.timflux.storyseek.core.user.mapper.UserMapper;
import cn.timflux.storyseek.core.user.service.AuthService;
import cn.timflux.storyseek.core.user.service.CaptchaService;
import cn.timflux.storyseek.core.user.service.UserService;
import cn.timflux.storyseek.core.write.edit.entity.Book;
import cn.timflux.storyseek.core.write.edit.entity.PromptSnippet;
import cn.timflux.storyseek.core.write.edit.entity.VolumeChapter;
import cn.timflux.storyseek.core.write.edit.service.BookService;
import cn.timflux.storyseek.core.write.promptsea.service.PromptSnippetFavoriteService;
import cn.timflux.storyseek.core.write.edit.service.PromptSnippetEditService;
import cn.timflux.storyseek.core.write.edit.service.VolumeChapterService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * ClassName: AuthServiceImpl
 * Package: cn.timflux.storyseek.core.user.service.impl
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/13 下午5:15
 * @Version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CaptchaService captchaService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final PromptSnippetEditService promptSnippetEditService;
    private final PromptSnippetFavoriteService favoriteService;
    private final BookService bookService;

    @Autowired
    private VolumeChapterService volumeChapterService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    // 手机号默认11位数字。注意暂不支持国际格式。
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");

    @Override
    public void sendCaptcha(String identifier) {
        captchaService.sendCaptcha(identifier);
    }

    @Override
    public User register(RegisterDTO dto) {
        String identifier = dto.getIdentifier();
        String password = dto.getPassword();

        if (!captchaService.verifyCaptcha(identifier, dto.getCaptcha())) {
            throw new RuntimeException("验证码错误");
        }

        if (userExists(identifier)) {
            throw new RuntimeException("账号已存在");
        }

        dto.setPassword(SaSecureUtil.md5(password));
        User user = userService.createUserInvite(dto);

        captchaService.removeCaptcha(identifier);

        // 注册后收藏默认提示词
        List<PromptSnippet> defaults = promptSnippetEditService.getDefaultSystemPromptSnippets();
        for (PromptSnippet snippet : defaults) {
            favoriteService.setFavorite(user.getId(), snippet.getId(), true);
        }
        // 注册后新建一本默认新书
        Book defaultBook = new Book();
        defaultBook.setUserId(user.getId());
        defaultBook.setTitle("我的新书");
        defaultBook.setType("长篇小说");
        defaultBook.setDescription("点击进入编辑页。");
        bookService.save(defaultBook);

        // 新建新书的第一卷
        VolumeChapter firstVolume = new VolumeChapter();
        firstVolume.setBookId(defaultBook.getId());
        firstVolume.setParentId(0L);
        firstVolume.setName("第一卷");
        firstVolume.setOrderNum(1);
        firstVolume.setType(1); // 卷
        volumeChapterService.createVolume(firstVolume);

        // 新建第一卷的第一章
        VolumeChapter firstChapter = new VolumeChapter();
        firstChapter.setBookId(defaultBook.getId());
        firstChapter.setParentId(firstVolume.getId());
        firstChapter.setName("第一章");
        firstChapter.setOrderNum(1);
        firstChapter.setType(2); // 章
        volumeChapterService.createChapter(firstChapter);

        return user;
    }

    @Override
    public void deleteAccount(Long userId, String rawPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");

        // 校验密码
        if (!user.getPassword().equals(SaSecureUtil.md5(rawPassword))) {
            throw new RuntimeException("密码错误");
        }

        // TODO：删除用户数据（包括关联数据，可考虑软删除或级联清理，暂不注销功能）
        userMapper.deleteById(userId);
        log.info("用户永久注销：{}", userId);

        // 清理关联表数据，例如收藏、创作记录、草稿等
    }


    @Override
    public User login(LoginDTO dto) {
        String idf = dto.getIdentifier();
        User user = getUserByIdentifier(idf);
        if (user == null) {
            throw new RuntimeException("用户不存在或凭据不匹配，请检查您的输入或先注册");
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (!user.getPassword().equals(SaSecureUtil.md5(dto.getPassword()))) {
                throw new RuntimeException("密码错误");
            }
        }
        else if (dto.getCaptcha() != null && !dto.getCaptcha().isBlank()) {
            if (!captchaService.verifyCaptcha(idf, dto.getCaptcha())) {
                throw new RuntimeException("验证码错误");
            }
            captchaService.removeCaptcha(idf);
        }
        else {
            throw new RuntimeException("请提供密码或验证码");
        }

        return user;
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 判断用户是否存在。
     * 分别通过邮箱和手机号进行查询。
     */
    private boolean userExists(String idf) {
        if (!StringUtils.hasText(idf)) {
            return false;
        }

        int countByEmail = Math.toIntExact(userMapper.selectCount(new QueryWrapper<User>().eq("email", idf)));
        if (countByEmail > 0) {
            return true;
        }

        if (PHONE_PATTERN.matcher(idf).matches() && !idf.equals("0")) {
            int countByPhone = Math.toIntExact(userMapper.selectCount(new QueryWrapper<User>().eq("phone", idf)));
            return countByPhone > 0;
        }
        return false;
    }

    /**
     * 根据标识符（邮箱或手机号）获取用户。
     * 分别通过邮箱和手机号进行查询。
     */
    private User getUserByIdentifier(String idf) {
        if (!StringUtils.hasText(idf)) {
            return null;
        }

        User user = null;

        if (EMAIL_PATTERN.matcher(idf).matches()) {
            user = userMapper.selectOne(new QueryWrapper<User>().eq("email", idf));
            if (user != null) {
                return user;
            }
        }

        if (PHONE_PATTERN.matcher(idf).matches() && !idf.equals("0")) {
            user = userMapper.selectOne(new QueryWrapper<User>().eq("phone", idf));
            return user;
        }
        return null;
    }
}