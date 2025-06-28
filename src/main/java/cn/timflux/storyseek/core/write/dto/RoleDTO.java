package cn.timflux.storyseek.core.write.dto;

import lombok.Data;

/**
 * ClassName: RoleDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/25 下午3:27
 * @Version 1.0
 */
@Data
public class RoleDTO {
    private Long id;
    private Long bookId;
    private String name;
    private String description;
}