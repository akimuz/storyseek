package cn.timflux.storyseek.core.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * ClassName: InspirationConsumeRecord
 * Package: cn.timflux.storyseek.core.user.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/7/19 下午6:52
 * @Version 1.0
 */
@Data
@TableName("inspiration_consume_record")
public class InspirationConsumeRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long consumeCount; // 消耗灵感值（字数）
    private String purpose;    // 用途
    private String requestInfo; // 请求相关信息
    @TableField(value = "create_time", insertStrategy = FieldStrategy.NEVER)
    private LocalDateTime createTime;
}
