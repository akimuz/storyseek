package cn.timflux.storyseek.core.write.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
}