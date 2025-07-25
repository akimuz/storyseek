package cn.timflux.storyseek.core.write.edit.dto;

import lombok.Data;

/**
* ClassName: BookDTO
* Package: cn.timflux.storyseek.core.write.dto
* Description:
* @Author 一剑霜寒十四州
* @Create 2025/6/25 下午5:41
* @Version 1.0
*/
@Data
public class BookDTO {
    private Long id;
    private String title;
    private String type;
    private String desc;
}
