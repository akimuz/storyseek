package cn.timflux.storyseek.core.write.dto;

import lombok.Data;

/**
 * ClassName: BookCreateDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/24 下午7:38
 * @Version 1.0
 */
@Data
public class BookCreateDTO {
    private String title;
    private String type;
    private String desc;
}
