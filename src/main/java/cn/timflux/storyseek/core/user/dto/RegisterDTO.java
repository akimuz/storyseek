package cn.timflux.storyseek.core.user.dto;

import lombok.Data;

/**
 * ClassName: RegisterDTO
 * Package: cn.timflux.storyseek.core.user.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/13 下午5:00
 * @Version 1.0
 */
@Data
public class RegisterDTO {
    private String identifier; // 邮箱或手机号
    private String password;
    private String captcha;
}