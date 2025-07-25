package cn.timflux.storyseek.core.write.edit.entity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ClassName: Role
 * Package: cn.timflux.storyseek.core.write.entity
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:26
 * @Version 1.0
 */
@Data
public class Role {
    private Long id;
    private Long bookId;
    private String name;
    private String description;
    private LocalDateTime createdAt;
}
