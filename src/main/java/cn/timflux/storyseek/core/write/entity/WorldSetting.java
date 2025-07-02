package cn.timflux.storyseek.core.write.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: WorldSetting
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:58
 * @Version 1.0
 */
@Data
@TableName("world_setting")
public class WorldSetting {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long bookId;
    private String name;
    private String description;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}