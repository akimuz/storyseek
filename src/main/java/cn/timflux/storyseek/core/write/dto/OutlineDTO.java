package cn.timflux.storyseek.core.write.dto;

import lombok.Data;

/**
 * ClassName: OutlineDTO
 * Package: cn.timflux.storyseek.core.write.dto
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/6/29 上午4:26
 * @Version 1.0
 */
@Data
public class OutlineDTO {
    private Long id;        // 更新/删除时必传，新增时可不传
    private Long bookId;
    private String title;
    private String content;
}
