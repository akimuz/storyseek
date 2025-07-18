package cn.timflux.storyseek.core.user.entity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * ClassName: User
 * Package: cn.timflux.storyseek.core.user.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午6:52
 * @Version 1.0
 */
@Data
@TableName("t_user")
public class User {
    private Long id;
    private String username;
    private String phone;
    private String email;
    private String password;
    private Long inspiration; // 代币
    private LocalDateTime createTime;
}
