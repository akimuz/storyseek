package cn.timflux.storyseek.core.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * ClassName: InviteCode
 * Package: cn.timflux.storyseek.core.user.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/19 下午6:52
 * @Version 1.0
 */
@Data
@TableName("invite_code")
public class InviteCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Boolean used;
    private LocalDateTime expireTime;
    private Long usedByUserId;
    private LocalDateTime createTime;
}

